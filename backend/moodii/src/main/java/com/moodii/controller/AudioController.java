package com.moodii.controller;

import com.moodii.model.Audio;
import com.moodii.dto.MoodPrediction;
import com.moodii.service.AudioFileService;
import com.moodii.service.MLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/audio")
public class AudioController {

    @Autowired
    private AudioFileService audioFileService;

    @Autowired
    private MLService mlService;

    @PostMapping("/upload")
    public ResponseEntity<Audio> uploadAudio(
            @RequestParam("log_id") Integer logId,
            @RequestParam("file") MultipartFile file) throws IOException {
        Audio audio = audioFileService.uploadAudio(logId, file);
        return ResponseEntity.ok(audio);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Audio> getAudio(@PathVariable Integer id) {
        Audio audioFile = audioFileService.getAudio(id);
        return audioFile != null ? ResponseEntity.ok(audioFile) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAudio(@PathVariable Integer id) {
        audioFileService.deleteAudio(id);
        return ResponseEntity.noContent().build();
    }

    // New ML Prediction Endpoint
    @PostMapping("/ml/predict")
    public ResponseEntity<MoodPrediction> predictMood(
            @RequestParam(value = "audio_id", required = false) Integer audioId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        
        return ResponseEntity.ok(mlService.predictMoodFromFile(file));
    }
}