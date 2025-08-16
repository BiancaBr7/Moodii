import os
import librosa
import numpy as np
import pandas as pd
from tqdm import tqdm

# Enhanced mood mapping for all datasets
MOOD_MAPPING = {
    # TESS emotions
    'happy': 'happy',
    'pleasant_surprise': 'happy',
    'sad': 'sad',
    'angry': 'angry',
    'fear': 'fear',
    'disgust': 'neutral',
    'neutral': 'neutral',
    
    # Crema emotions
    'HAP': 'happy',
    'ANG': 'angry',
    'SAD': 'sad',
    'FEA': 'fear',
    'NEU': 'neutral',
    'DIS': 'neutral',
    
    # RAVDESS emotions
    '01': 'neutral',  # neutral
    '02': 'happy',    # calm -> happy (similar to pleasant)
    '03': 'happy',    # happy
    '04': 'sad',      # sad
    '05': 'angry',    # angry
    '06': 'fear',     # fearful
    '07': 'neutral',  # disgust
    '08': 'happy',    # surprised -> happy (similar to pleasant surprise)
    
    # SAVEE emotions
    'a': 'angry',
    'd': 'neutral',   # disgust -> neutral
    'f': 'fear',
    'h': 'happy',
    'n': 'neutral',
    'sa': 'sad',
    'su': 'happy'     # surprise -> happy
}

# Get the absolute path to the project root directory
PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def extract_features(file_path):
    """Extract audio features using librosa"""
    try:
        audio, sample_rate = librosa.load(file_path, res_type='kaiser_fast')
        mfccs = librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=40)
        mfccs_scaled = np.mean(mfccs.T, axis=0)
        return mfccs_scaled
    except Exception as e:
        print(f"Error processing {file_path}: {str(e)}")
        return None

def process_tess_dataset(data_path):
    """Process TESS Toronto dataset"""
    features = []
    labels = []
    
    if not os.path.exists(data_path):
        print(f"Warning: TESS dataset path not found: {data_path}")
        return features, labels
    
    for root, dirs, files in os.walk(data_path):
        for file in files:
            if file.endswith('.wav'):
                try:
                    emotion = file.split('_')[-1].split('.')[0].lower()
                    general_mood = MOOD_MAPPING.get(emotion, 'neutral')
                    file_path = os.path.join(root, file)
                    feature = extract_features(file_path)
                    if feature is not None:
                        features.append(feature)
                        labels.append(general_mood)
                except Exception as e:
                    print(f"Error processing {file}: {str(e)}")
    return features, labels

def process_crema_dataset(data_path):
    """Process Crema dataset"""
    features = []
    labels = []
    
    if not os.path.exists(data_path):
        print(f"Warning: Crema dataset path not found: {data_path}")
        return features, labels
    
    for file in os.listdir(data_path):
        if file.endswith('.wav'):
            try:
                parts = file.split('_')
                if len(parts) >= 3:  # Ensure filename has expected structure
                    emotion = parts[2]
                    general_mood = MOOD_MAPPING.get(emotion, 'neutral')
                    file_path = os.path.join(data_path, file)
                    feature = extract_features(file_path)
                    if feature is not None:
                        features.append(feature)
                        labels.append(general_mood)
            except Exception as e:
                print(f"Error processing {file}: {str(e)}")
    return features, labels

def process_ravdess_dataset(data_path):
    """Process RAVDESS dataset"""
    features = []
    labels = []
    
    for file in os.listdir(data_path):
        if file.endswith('.wav'):
            emotion_code = file.split('-')[2]
            general_mood = MOOD_MAPPING.get(emotion_code, 'neutral')
            file_path = os.path.join(data_path, file)
            feature = extract_features(file_path)
            if feature is not None:
                features.append(feature)
                labels.append(general_mood)
    return features, labels

def process_savee_dataset(data_path):
    """Process SAVEE dataset"""
    features = []
    labels = []
    
    for file in os.listdir(data_path):
        if file.endswith('.wav'):
            emotion_code = file[:2] if file.startswith('sa') else file[0]
            general_mood = MOOD_MAPPING.get(emotion_code, 'neutral')
            file_path = os.path.join(data_path, file)
            feature = extract_features(file_path)
            if feature is not None:
                features.append(feature)
                labels.append(general_mood)
    return features, labels

def save_processed_data(features, labels, output_dir):
    """Save processed features and labels to CSV files"""
    try:
        # Convert features to DataFrame
        features_df = pd.DataFrame(features)
        
        # Convert labels to DataFrame
        labels_df = pd.DataFrame(labels, columns=['mood'])
        
        # Save to CSV
        features_df.to_csv(os.path.join(output_dir, 'features.csv'), index=False)
        labels_df.to_csv(os.path.join(output_dir, 'labels.csv'), index=False)
    except Exception as e:
        print(f"Error saving processed data: {str(e)}")

def main():
    # Use absolute paths based on project root
    data_dir = os.path.join(PROJECT_ROOT, 'data')
    output_dir = os.path.join(PROJECT_ROOT, 'processed_data')
    
    # Create output directory if it doesn't exist
    os.makedirs(output_dir, exist_ok=True)
    
    all_features = []
    all_labels = []
    
    # Process all datasets with error handling
    print("Processing TESS dataset...")
    tess_path = os.path.join(data_dir, 'TESS Toronto emotional speech set data')
    tess_features, tess_labels = process_tess_dataset(tess_path)
    all_features.extend(tess_features)
    all_labels.extend(tess_labels)
    
    print("Processing Crema dataset...")
    crema_path = os.path.join(data_dir, 'Crema')
    crema_features, crema_labels = process_crema_dataset(crema_path)
    all_features.extend(crema_features)
    all_labels.extend(crema_labels)
    
    print("Processing RAVDESS dataset...")
    ravdess_path = os.path.join(data_dir, 'RAVDESS')
    ravdess_features, ravdess_labels = process_ravdess_dataset(ravdess_path)
    all_features.extend(ravdess_features)
    all_labels.extend(ravdess_labels)
    
    print("Processing SAVEE dataset...")
    savee_path = os.path.join(data_dir, 'SAVEE')
    savee_features, savee_labels = process_savee_dataset(savee_path)
    all_features.extend(savee_features)
    all_labels.extend(savee_labels)
    
    # Save processed data
    if all_features:
        save_processed_data(all_features, all_labels, output_dir)
        print(f"Processed data saved to {output_dir}")
    else:
        print("Error: No features were extracted. Check dataset paths.")

if __name__ == "__main__":
    main()