package com.moodii.controller;

import com.moodii.model.Audio;
import com.moodii.service.AudioFileService;
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

    @PostMapping("/upload")
    public ResponseEntity<Audio> uploadAudio(
            @RequestParam("log_id") Integer logId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(audioFileService.uploadAudio(logId, file));
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
}