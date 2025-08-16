package com.moodii.controller;

import com.moodii.dto.MlPredictionResponse;
import com.moodii.service.MlClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ml")
@CrossOrigin(origins = "*")
public class MlController {

    private final MlClientService mlClientService;

    public MlController(MlClientService mlClientService) {
        this.mlClientService = mlClientService;
    }

    @PostMapping("/predict")
    public ResponseEntity<MlPredictionResponse> predict(@RequestParam("audio") MultipartFile audio) throws Exception {
        if(audio==null || audio.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        MlPredictionResponse resp = mlClientService.predictEmotion(audio);
        return ResponseEntity.ok(resp);
    }
}
