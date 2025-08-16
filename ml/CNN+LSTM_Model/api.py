from flask import Flask, request, jsonify
from flask_cors import CORS
import numpy as np
import librosa
import io
import os
import sys
from tensorflow.keras.models import load_model
import logging
from datetime import datetime
import traceback
from werkzeug.utils import secure_filename
import hashlib
import time

# Configure production-level logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('emotion_api.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

app = Flask(__name__)

# Production CORS configuration
CORS(app, 
     origins=["http://localhost:3000", "http://localhost:8080", "https://yourdomain.com"],
     methods=["GET", "POST"],
     allow_headers=["Content-Type", "Authorization"])

# Configuration
class Config:
    MAX_CONTENT_LENGTH = 16 * 1024 * 1024  # 16MB max file size
    ALLOWED_EXTENSIONS = {'wav', 'mp3', 'flac', 'm4a', 'ogg'}
    MODEL_PATH = "model_CLSTM_20_82.h5"
    FEATURE_LENGTH = 100
    N_MFCC = 13
    SAMPLE_RATE = 22050
    REQUEST_TIMEOUT = 30  # seconds

app.config.from_object(Config)

# Global variables
model = None
model_load_time = None
request_count = 0
emotion_labels = ['angry', 'disgust', 'fear', 'happy', 'neutral', 'sad', 'surprise']

def allowed_file(filename):
    """Check if the uploaded file has an allowed extension"""
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in app.config['ALLOWED_EXTENSIONS']

def validate_request():
    """Validate incoming request"""
    global request_count
    request_count += 1
    
    # Log request details
    logger.info(f"Request #{request_count} from {request.remote_addr}")
    
    # Check content length
    if request.content_length and request.content_length > app.config['MAX_CONTENT_LENGTH']:
        return {"error": "File too large. Maximum size is 16MB."}, 413
    
    return None, 200

def load_emotion_model():
    """Load the CNN+LSTM emotion recognition model with error handling"""
    global model, model_load_time
    try:
        script_dir = os.path.dirname(os.path.abspath(__file__))
        model_path = os.path.join(script_dir, app.config['MODEL_PATH'])
        
        if not os.path.exists(model_path):
            logger.error(f"Model file not found at {model_path}")
            return False
            
        logger.info(f"Loading model from: {model_path}")
        start_time = time.time()
        model = load_model(model_path)
        model_load_time = time.time() - start_time
        
        logger.info(f"Model loaded successfully in {model_load_time:.2f} seconds!")
        logger.info(f"Model input shape: {model.input_shape}")
        logger.info(f"Model output shape: {model.output_shape}")
        
        # Warm up the model with a dummy prediction
        dummy_input = np.random.random((1, app.config['FEATURE_LENGTH'], app.config['N_MFCC']))
        _ = model.predict(dummy_input, verbose=0)
        logger.info("Model warmed up successfully")
        
        return True
    except Exception as e:
        logger.error(f"Error loading model: {e}")
        logger.error(traceback.format_exc())
        return False

def extract_features(audio_data, sample_rate=None):
    """
    Extract features from audio data for CNN+LSTM model with robust error handling
    
    Args:
        audio_data: Audio time series
        sample_rate: Sample rate of audio
    
    Returns:
        numpy array of features or None if extraction fails
    """
    try:
        if sample_rate is None:
            sample_rate = app.config['SAMPLE_RATE']
            
        # Validate audio data
        if len(audio_data) == 0:
            logger.error("Empty audio data")
            return None
            
        if len(audio_data) < sample_rate * 0.1:  # Minimum 0.1 seconds
            logger.warning("Audio too short, padding...")
            # Pad short audio
            padding_length = int(sample_rate * 0.5) - len(audio_data)
            audio_data = np.pad(audio_data, (0, padding_length), 'constant')
        
        # Extract MFCC features with error handling
        try:
            mfccs = librosa.feature.mfcc(
                y=audio_data, 
                sr=sample_rate, 
                n_mfcc=app.config['N_MFCC'],
                hop_length=512,
                n_fft=2048
            )
        except Exception as e:
            logger.error(f"MFCC extraction failed: {e}")
            return None
        
        # Transpose to get (time_steps, features) format
        mfccs = mfccs.T
        
        # Handle length normalization
        max_length = app.config['FEATURE_LENGTH']
        if len(mfccs) > max_length:
            # Take middle portion if too long
            start_idx = (len(mfccs) - max_length) // 2
            mfccs = mfccs[start_idx:start_idx + max_length]
        else:
            # Pad with zeros if too short
            pad_width = max_length - len(mfccs)
            mfccs = np.pad(mfccs, ((0, pad_width), (0, 0)), mode='constant')
        
        # Normalize features
        mfccs = (mfccs - np.mean(mfccs)) / (np.std(mfccs) + 1e-8)
        
        return mfccs
        
    except Exception as e:
        logger.error(f"Error extracting features: {e}")
        logger.error(traceback.format_exc())
        return None

def predict_emotion(features, request_id=None):
    """
    Predict emotion from audio features with comprehensive error handling
    
    Args:
        features: Extracted audio features
        request_id: Optional request identifier for logging
    
    Returns:
        Dictionary with prediction results
    """
    global model
    
    if model is None:
        logger.error("Model not loaded for prediction")
        return {"error": "Model not loaded", "status": "model_error"}
    
    try:
        start_time = time.time()
        
        # Validate features
        if features is None or features.size == 0:
            return {"error": "Invalid features", "status": "feature_error"}
        
        # Add batch dimension
        features = np.expand_dims(features, axis=0)
        
        # Make prediction with timeout protection
        predictions = model.predict(features, verbose=0)
        prediction_time = time.time() - start_time
        
        # Validate prediction output
        if predictions is None or len(predictions) == 0:
            return {"error": "Model prediction failed", "status": "prediction_error"}
        
        # Get predicted class and confidence
        predicted_class = np.argmax(predictions[0])
        confidence = float(predictions[0][predicted_class])
        
        # Get all class probabilities
        all_predictions = {}
        for i, label in enumerate(emotion_labels[:len(predictions[0])]):
            all_predictions[label] = float(predictions[0][i])
        
        # Calculate entropy for uncertainty estimation
        entropy = -np.sum(predictions[0] * np.log(predictions[0] + 1e-8))
        
        result = {
            "predicted_emotion": emotion_labels[predicted_class] if predicted_class < len(emotion_labels) else f"class_{predicted_class}",
            "confidence": confidence,
            "all_predictions": all_predictions,
            "uncertainty": float(entropy),
            "prediction_time": prediction_time,
            "status": "success"
        }
        
        # Log prediction details
        if request_id:
            logger.info(f"Request {request_id}: Predicted {result['predicted_emotion']} with confidence {confidence:.3f} in {prediction_time:.3f}s")
        
        return result
        
    except Exception as e:
        logger.error(f"Error during prediction: {e}")
        logger.error(traceback.format_exc())
        return {"error": str(e), "status": "prediction_error"}

@app.route('/health', methods=['GET'])
def health_check():
    """Comprehensive health check endpoint"""
    global model, model_load_time, request_count
    # Attempt lazy load if model not yet loaded (e.g., gunicorn import path)
    if model is None:
        logger.info("Health check triggered model load attempt (model was None)")
        load_emotion_model()
    
    health_status = {
        "status": "healthy" if model is not None else "unhealthy",
        "model_loaded": model is not None,
        "model_load_time": model_load_time,
        "total_requests": request_count,
        "timestamp": datetime.utcnow().isoformat(),
        "version": "1.0.0",
        "message": "CNN+LSTM Emotion Recognition API"
    }
    
    status_code = 200 if model is not None else 503
    return jsonify(health_status), status_code

@app.route('/model-info', methods=['GET'])
def model_info():
    """Get comprehensive model information"""
    global model
    
    if model is None:
        return jsonify({"error": "Model not loaded", "status": "model_error"}), 503
    
    try:
        info = {
            "model_type": "CNN+LSTM",
            "model_name": "model_CLSTM_20_82",
            "input_shape": model.input_shape,
            "output_shape": model.output_shape,
            "total_parameters": int(model.count_params()),
            "emotion_labels": emotion_labels,
            "feature_config": {
                "n_mfcc": app.config['N_MFCC'],
                "feature_length": app.config['FEATURE_LENGTH'],
                "sample_rate": app.config['SAMPLE_RATE']
            },
            "supported_formats": list(app.config['ALLOWED_EXTENSIONS']),
            "max_file_size_mb": app.config['MAX_CONTENT_LENGTH'] // (1024 * 1024),
            "description": "Convolutional Neural Network + Long Short-Term Memory model for emotion recognition",
            "status": "ready"
        }
        return jsonify(info)
    except Exception as e:
        logger.error(f"Error getting model info: {e}")
        return jsonify({"error": str(e), "status": "error"}), 500

@app.route('/predict', methods=['POST'])
def predict():
    """
    Production-ready emotion prediction endpoint
    
    Expected: multipart/form-data with 'audio' file field
    Returns: JSON with emotion prediction and metadata
    """
    request_id = hashlib.md5(f"{request.remote_addr}{time.time()}".encode()).hexdigest()[:8]
    
    try:
        # Validate request
        validation_error, status_code = validate_request()
        if validation_error:
            return jsonify(validation_error), status_code
        
        # Check if model is loaded
        if model is None:
            logger.error(f"Request {request_id}: Model not loaded")
            return jsonify({"error": "Model not loaded", "status": "model_error", "request_id": request_id}), 503
        
        # Check if audio file is provided
        if 'audio' not in request.files:
            logger.warning(f"Request {request_id}: No audio file provided")
            return jsonify({"error": "No audio file provided", "status": "input_error", "request_id": request_id}), 400
        
        audio_file = request.files['audio']
        
        if audio_file.filename == '':
            return jsonify({"error": "No audio file selected", "status": "input_error", "request_id": request_id}), 400
        
        # Validate file extension
        if not allowed_file(audio_file.filename):
            return jsonify({
                "error": f"Unsupported file format. Allowed: {', '.join(app.config['ALLOWED_EXTENSIONS'])}", 
                "status": "format_error", 
                "request_id": request_id
            }), 400
        
        # Secure filename
        filename = secure_filename(audio_file.filename)
        logger.info(f"Request {request_id}: Processing file {filename}")
        
        # Read audio file
        audio_bytes = audio_file.read()
        
        if len(audio_bytes) == 0:
            return jsonify({"error": "Empty audio file", "status": "input_error", "request_id": request_id}), 400
        
        # Load audio using librosa with error handling
        try:
            audio_data, sample_rate = librosa.load(io.BytesIO(audio_bytes), sr=app.config['SAMPLE_RATE'])
        except Exception as e:
            logger.error(f"Request {request_id}: Failed to load audio - {e}")
            return jsonify({
                "error": f"Failed to load audio file: {str(e)}", 
                "status": "audio_error", 
                "request_id": request_id
            }), 400
        
        # Extract features
        features = extract_features(audio_data, sample_rate)
        
        if features is None:
            logger.error(f"Request {request_id}: Feature extraction failed")
            return jsonify({
                "error": "Failed to extract features from audio", 
                "status": "feature_error", 
                "request_id": request_id
            }), 500
        
        # Predict emotion
        result = predict_emotion(features, request_id)
        
        if result.get("status") != "success":
            return jsonify({**result, "request_id": request_id}), 500
        
        # Add metadata
        result.update({
            "request_id": request_id,
            "filename": filename,
            "audio_duration": float(len(audio_data) / sample_rate),
            "sample_rate": sample_rate,
            "features_shape": features.shape,
            "model_type": "CNN+LSTM",
            "timestamp": datetime.utcnow().isoformat()
        })
        
        return jsonify(result)
        
    except Exception as e:
        logger.error(f"Request {request_id}: Unexpected error - {e}")
        logger.error(traceback.format_exc())
        return jsonify({
            "error": "Internal server error", 
            "status": "server_error", 
            "request_id": request_id
        }), 500

@app.route('/predict-batch', methods=['POST'])
def predict_batch():
    """
    Predict emotions for multiple audio files
    
    Expected: multipart/form-data with multiple 'audio' file fields
    Returns: JSON with batch emotion predictions
    """
    try:
        # Check if model is loaded
        if model is None:
            return jsonify({"error": "Model not loaded"}), 500
        
        # Check if audio files are provided
        if 'audio' not in request.files:
            return jsonify({"error": "No audio files provided"}), 400
        
        audio_files = request.files.getlist('audio')
        
        if not audio_files:
            return jsonify({"error": "No audio files selected"}), 400
        
        results = []
        
        for i, audio_file in enumerate(audio_files):
            try:
                # Read and process each audio file
                audio_bytes = audio_file.read()
                audio_data, sample_rate = librosa.load(io.BytesIO(audio_bytes), sr=22050)
                
                # Extract features
                features = extract_features(audio_data, sample_rate)
                
                if features is not None:
                    # Predict emotion
                    result = predict_emotion(features)
                    result.update({
                        "filename": audio_file.filename,
                        "file_index": i,
                        "audio_duration": float(len(audio_data) / sample_rate)
                    })
                else:
                    result = {
                        "filename": audio_file.filename,
                        "file_index": i,
                        "error": "Failed to extract features"
                    }
                
                results.append(result)
                
            except Exception as e:
                results.append({
                    "filename": audio_file.filename,
                    "file_index": i,
                    "error": str(e)
                })
        
        return jsonify({
            "batch_results": results,
            "total_files": len(audio_files),
            "successful_predictions": len([r for r in results if "error" not in r])
        })
        
    except Exception as e:
        logger.error(f"Error in predict_batch endpoint: {e}")
        return jsonify({"error": str(e)}), 500

@app.errorhandler(404)
def not_found(error):
    return jsonify({
        "error": "Endpoint not found",
        "status": "not_found",
        "available_endpoints": ["/health", "/model-info", "/predict", "/predict-batch"]
    }), 404

@app.errorhandler(413)
def request_entity_too_large(error):
    return jsonify({
        "error": f"File too large. Maximum size is {app.config['MAX_CONTENT_LENGTH'] // (1024*1024)}MB",
        "status": "file_too_large"
    }), 413

@app.errorhandler(500)
def internal_error(error):
    logger.error(f"Internal server error: {error}")
    return jsonify({
        "error": "Internal server error",
        "status": "server_error"
    }), 500

@app.before_request
def log_request_info():
    """Log incoming requests for monitoring"""
    logger.info(f"Request: {request.method} {request.url} from {request.remote_addr}")

@app.after_request
def log_response_info(response):
    """Log response details"""
    logger.info(f"Response: {response.status_code} for {request.method} {request.url}")
    return response

"""Pre-load model at import so gunicorn workers have it ready."""
if load_emotion_model():
    logger.info("Model pre-loaded successfully during module import.")
else:
    logger.error("Model failed to load during module import; will retry lazily on /health or first prediction.")

if __name__ == '__main__':
    # Only used for local dev (not in gunicorn path)
    if model is None:
        load_emotion_model()
    app.run(debug=False, host='0.0.0.0', port=5000, threaded=True, use_reloader=False)
