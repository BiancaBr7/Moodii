package com.moodii.dto;

import lombok.Data;
import java.util.Map;

@Data
public class MlPredictionResponse {
    private String predictedEmotion;
    private Double confidence;
    private Map<String, Double> allPredictions;
    private String status;
    private String error;
    private String requestId;
}
