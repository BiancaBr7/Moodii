package com.moodii.controller;

import com.moodii.model.AudioFile;
import com.moodii.service.AudioFileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    private final AudioFileService audioFileService;

    public AudioController(AudioFileService audioFileService) {
        this.audioFileService = audioFileService;
    }

    @PostMapping("/{logId}")
    public AudioFile uploadAudio(
            @PathVariable Integer logId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return audioFileService.saveAudioFile(logId, file);
    }

    @GetMapping("/{logId}")
    public AudioFile getAudioFile(@PathVariable Integer logId) {
        return audioFileService.getAudioFileByLogId(logId);
    }
}