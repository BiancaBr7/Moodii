# Audio Transcription Setup Guide

This guide explains how to set up auto-transcription functionality for the Moodii app using multiple speech-to-text options.

## Available Transcription Methods

### 1. Android Built-in Speech Recognition (Default)
- **Pros**: Free, works offline, no API setup required
- **Cons**: Limited accuracy, primarily designed for live speech
- **Setup**: Already integrated and working

### 2. Google Cloud Speech-to-Text API (Recommended)
- **Pros**: High accuracy, supports many languages, handles audio files well
- **Cons**: Requires Google Cloud account, costs money after free tier
- **Best for**: Production apps with high accuracy requirements

### 3. OpenAI Whisper API (Alternative)
- **Pros**: Very high accuracy, good for various audio qualities
- **Cons**: Requires OpenAI account, API costs
- **Best for**: High-quality transcription needs

## Setup Instructions

### Option A: Use Android Built-in (Already Working)
The app already includes Android's built-in speech recognition:
- Real-time transcription during recording
- No additional setup required
- Works on emulator and physical devices

### Option B: Google Cloud Speech-to-Text Setup

1. **Create Google Cloud Project**:
   ```bash
   # Go to https://console.cloud.google.com/
   # Create new project or select existing one
   ```

2. **Enable Speech-to-Text API**:
   ```bash
   # In Google Cloud Console:
   # APIs & Services > Library > Search "Speech-to-Text" > Enable
   ```

3. **Create API Key**:
   ```bash
   # APIs & Services > Credentials > Create Credentials > API Key
   # Copy the generated API key
   ```

4. **Add API Key to App**:
   ```kotlin
   // In CloudSpeechTranscriber.kt, replace:
   private val apiKey = "YOUR_GOOGLE_CLOUD_API_KEY"
   // With your actual API key:
   private val apiKey = "AIzaSyC-your-actual-api-key-here"
   ```

5. **Update AudioRecorderViewModel**:
   ```kotlin
   // Add CloudSpeechTranscriber to AudioRecorderViewModel.kt
   private val cloudTranscriber = CloudSpeechTranscriber(application)
   
   // In transcribeAudioFile method, use cloud transcription:
   private fun transcribeAudioFile(audioFile: File) {
       viewModelScope.launch {
           try {
               _state.value = _state.value.copy(isTranscribing = true)
               val result = cloudTranscriber.transcribeAudioFile(audioFile)
               result.onSuccess { transcription ->
                   _state.value = _state.value.copy(
                       transcription = transcription,
                       isTranscribing = false,
                       transcriptionError = null
                   )
               }.onFailure { error ->
                   _state.value = _state.value.copy(
                       transcriptionError = "Cloud transcription failed: ${error.message}",
                       isTranscribing = false
                   )
               }
           } catch (e: Exception) {
               _state.value = _state.value.copy(
                   transcriptionError = "Failed to transcribe audio: ${e.message}",
                   isTranscribing = false
               )
           }
       }
   }
   ```

### Option C: OpenAI Whisper Setup

1. **Get OpenAI API Key**:
   ```bash
   # Go to https://platform.openai.com/api-keys
   # Create new API key
   ```

2. **Add API Key**:
   ```kotlin
   // In CloudSpeechTranscriber.kt, replace:
   val whisperApiKey = "YOUR_OPENAI_API_KEY"
   // With your actual key
   ```

3. **Use Whisper in ViewModel**:
   ```kotlin
   // Replace cloud transcription call with:
   val result = cloudTranscriber.transcribeWithWhisper(audioFile)
   ```

## Current Implementation Status

✅ **Android Built-in Speech Recognition**: Fully implemented
- Real-time transcription during recording
- Handles permission requests
- Shows transcription in UI with live updates
- Fallback for audio file transcription

✅ **UI Integration**: Complete
- Live transcription display
- Recording indicator with microphone animation
- Error handling and status messages
- Scrollable transcription text

⚠️ **Cloud Services**: Code ready, API keys needed
- Google Cloud Speech-to-Text integration complete
- OpenAI Whisper integration complete
- Just need to add your API keys

## Testing the Transcription

### On Physical Device:
1. Install app on real Android device
2. Grant microphone permission
3. Start recording and speak clearly
4. Watch real-time transcription appear

### On Emulator:
- Built-in speech recognition may not work on emulator
- Emulator typically doesn't have microphone access
- Use physical device for full testing

## Transcription Features Included

### Real-time Features:
- ✅ Live transcription during recording
- ✅ Visual recording indicator
- ✅ Permission handling
- ✅ Error messages and fallbacks

### UI Features:
- ✅ Transcription display box
- ✅ Scrollable text for long transcriptions
- ✅ Loading states and indicators
- ✅ Error state handling

### Backend Integration:
- ✅ Transcription saved with mood logs
- ✅ Accessible in mood log history
- ✅ Searchable transcription text

## Costs and Limits

### Android Built-in: 
- **Cost**: Free
- **Limits**: Device dependent

### Google Cloud Speech-to-Text:
- **Free Tier**: 60 minutes/month
- **Cost**: $0.006 per 15 seconds after free tier
- **Limits**: 10MB file size, 1 hour audio length

### OpenAI Whisper:
- **Cost**: $0.006 per minute
- **Limits**: 25MB file size
- **Accuracy**: Generally highest quality

## Recommendation

For development and testing: Use the built-in Android speech recognition (already working).

For production: Consider Google Cloud Speech-to-Text for the best balance of accuracy and cost.

For highest accuracy: Use OpenAI Whisper, especially for longer recordings or noisy environments.
