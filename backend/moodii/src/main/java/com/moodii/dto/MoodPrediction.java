package com.moodii.dto;

public record MoodPrediction(
    Integer moodId,
    String moodType,
    String emoji,
    String description,
    String transcription,
    Double confidence
) {}