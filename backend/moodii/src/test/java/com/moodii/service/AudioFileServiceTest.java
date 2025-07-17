package com.moodii.service;

import com.moodii.model.AudioFile;
import com.moodii.repository.AudioFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AudioFileServiceTest {

    @Mock
    private AudioFileRepository audioFileRepository;

    @InjectMocks
    private AudioFileService audioFileService;

    @Test
    public void saveAudioFile_SavesAndReturnsAudioFile() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.mp3", "audio/mpeg", "content".getBytes());
        AudioFile expected = new AudioFile("1", 1, 123, file.getBytes(), "mp3");
        
        when(audioFileRepository.save(any(AudioFile.class))).thenReturn(expected);

        // Act
        AudioFile result = audioFileService.saveAudioFile(123, file);

        // Assert
        assertNotNull(result);
        assertEquals(123, result.getLogId());
        verify(audioFileRepository, times(1)).save(any(AudioFile.class));
    }

    @Test
    public void getAudioFileByLogId_ReturnsAudioFile_WhenExists() {
        // Arrange
        AudioFile expected = new AudioFile("1", 1, 123, "content".getBytes(), "mp3");
        when(audioFileRepository.findByLogId(123)).thenReturn(expected);

        // Act
        AudioFile result = audioFileService.getAudioFileByLogId(123);

        // Assert
        assertNotNull(result);
        assertEquals(123, result.getLogId());
    }
}