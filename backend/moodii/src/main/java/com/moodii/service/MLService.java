package com.moodii.service;

import com.moodii.model.Mood;
import com.moodii.dto.MoodPrediction;
import com.moodii.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
public class MLService {
    
    @Autowired
    private MoodRepository moodRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${ml.service.url}")
    private String mlServiceUrl;
    
    @SuppressWarnings("unchecked")
    public MoodPrediction predictMoodFromFile(MultipartFile file) {
        Map<String, Object> response = restTemplate.postForObject(
            mlServiceUrl + "/predict",
            file,
            Map.class);
        
        String predictedMoodType = ((String) response.get("mood_type")).toLowerCase();
        Mood mood = moodRepository.findByTypeIgnoreCase(predictedMoodType)
            .orElseThrow(() -> new RuntimeException("Unsupported mood type: " + predictedMoodType));
        
        return new MoodPrediction(
            mood.getMoodId(),
            mood.getType(),
            mood.getEmoji(),
            mood.getDescription(),
            (String) response.get("transcription"),
            (Double) response.get("confidence")
        );
    }
}