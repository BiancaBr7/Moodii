package com.moodii.exception;

public class MoodLogNotFoundException extends RuntimeException {
    public MoodLogNotFoundException(String message) {
        super(message);
    }
    
    public MoodLogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
