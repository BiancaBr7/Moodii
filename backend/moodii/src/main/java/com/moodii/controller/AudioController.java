package com.moodii.controller;

import com.moodii.model.AudioFile;
import com.moodii.service.AudioFileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/audio")
public class AudioController {
    private final AudioFileService audioFileService;

    public AudioController(AudioFileService audioFileService) {
        this.audioFileService = audioFileService;
    }

    @PostMapping("/{logId}")
    public AudioFile uploadAudio(
            @PathVariable String logId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return audioFileService.saveAudioFile(logId, file);
    }

    @PostMapping("/upload")
    public AudioFile uploadAudioWithParam(
            @RequestParam("logId") String logId,
            @RequestParam("audio") MultipartFile file) throws IOException {
        return audioFileService.saveAudioFile(logId, file);
    }

    @GetMapping("/{logId}")
    public Optional<AudioFile> getAudioFile(@PathVariable String logId) {
        return audioFileService.getAudioFileByLogId(logId);
    }
}