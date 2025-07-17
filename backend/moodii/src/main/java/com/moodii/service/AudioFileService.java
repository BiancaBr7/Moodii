package com.moodii.service;

import com.moodii.model.AudioFile;
import com.moodii.repository.AudioFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class AudioFileService {
    private final AudioFileRepository audioFileRepository;

    public AudioFileService(AudioFileRepository audioFileRepository) {
        this.audioFileRepository = audioFileRepository;
    }

    public AudioFile saveAudioFile(Integer logId, MultipartFile file) throws IOException {
        AudioFile audioFile = new AudioFile();
        audioFile.setLogId(logId);
        audioFile.setAudioBlob(file.getBytes());
        audioFile.setFormat(file.getContentType());
        return audioFileRepository.save(audioFile);
    }

    public AudioFile getAudioFileByLogId(Integer logId) {
        return audioFileRepository.findByLogId(logId);
    }
}