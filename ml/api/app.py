from flask import Flask, request, jsonify
import librosa
import numpy as np
import joblib
import os

app = Flask(__name__)

# Load model and encoder
model = joblib.load('../models/mood_model.pkl')
label_encoder = joblib.load('../models/label_encoder.pkl')

def extract_features(audio_path):
    """Extract features from audio file"""
    audio, sample_rate = librosa.load(audio_path, res_type='kaiser_fast')
    mfccs = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=40)
    mfccs_scaled = np.mean(mfccs.T, axis=0)
    return mfccs_scaled.reshape(1, -1)

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file provided'}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({'error': 'No file selected'}), 400
    
    try:
        # Save temporary file
        temp_path = os.path.join('/tmp', file.filename)
        file.save(temp_path)
        
        # Extract features and predict
        features = extract_features(temp_path)
        prediction = model.predict(features)
        mood = label_encoder.inverse_transform(prediction)[0]
        
        # Clean up
        os.remove(temp_path)
        
        return jsonify({'mood': mood})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)