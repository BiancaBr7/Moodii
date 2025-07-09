package com.moodii.service;

import com.moodii.model.Audio;
import com.moodii.repository.AudioFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class AudioFileService {

    @Autowired
    private AudioFileRepository audioFileRepository;

    public Audio uploadAudio(Integer logId, MultipartFile file) throws IOException {
        Audio audioFile = new Audio();
        audioFile.setLogId(logId);
        audioFile.setAudioBlob(file.getBytes());
        audioFile.setFormat(file.getContentType());
        return audioFileRepository.save(audioFile);
    }

    public Audio getAudio(Integer id) {
        return audioFileRepository.findById(id).orElse(null);
    }

    public void deleteAudio(Integer id) {
        audioFileRepository.deleteById(id);
    }
}