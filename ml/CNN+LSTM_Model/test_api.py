import requests
import os

# Test script for the CNN+LSTM Emotion Recognition API

API_BASE_URL = "http://localhost:5000"

def test_health():
    """Test the health endpoint"""
    print("Testing health endpoint...")
    try:
        response = requests.get(f"{API_BASE_URL}/health")
        print(f"Status: {response.status_code}")
        print(f"Response: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"Error: {e}")
        return False

def test_model_info():
    """Test the model info endpoint"""
    print("\nTesting model info endpoint...")
    try:
        response = requests.get(f"{API_BASE_URL}/model-info")
        print(f"Status: {response.status_code}")
        print(f"Response: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"Error: {e}")
        return False

def test_prediction(audio_file_path):
    """Test the prediction endpoint with an audio file"""
    print(f"\nTesting prediction with {audio_file_path}...")
    
    if not os.path.exists(audio_file_path):
        print(f"Audio file not found: {audio_file_path}")
        return False
    
    try:
        with open(audio_file_path, 'rb') as f:
            files = {'audio': f}
            response = requests.post(f"{API_BASE_URL}/predict", files=files)
        
        print(f"Status: {response.status_code}")
        print(f"Response: {response.json()}")
        return response.status_code == 200
    except Exception as e:
        print(f"Error: {e}")
        return False

def main():
    print("CNN+LSTM Emotion Recognition API Test")
    print("=" * 50)
    
    # Test health
    if not test_health():
        print("Health check failed. Make sure the API is running.")
        return
    
    # Test model info
    test_model_info()
    
    # Test prediction with sample audio files from CREMA dataset
    sample_audio_files = [
        "../../data/Crema/1001_DFA_ANG_XX.wav",
        "../../data/Crema/1001_DFA_HAP_XX.wav",
        "../../data/Crema/1001_DFA_SAD_XX.wav"
    ]
    
    for audio_file in sample_audio_files:
        test_prediction(audio_file)
    
    print("\n" + "=" * 50)
    print("Test completed!")

if __name__ == "__main__":
    main()
