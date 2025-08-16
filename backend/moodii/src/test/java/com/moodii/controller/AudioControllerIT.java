package com.moodii.controller;

import com.moodii.model.AudioFile;
import com.moodii.repository.AudioFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AudioControllerIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AudioFileRepository audioFileRepository;

    @BeforeEach
    void setUp() {
        audioFileRepository.deleteAll();
    }

    @Test
    public void uploadAndGetAudio_ReturnsAudioFile() throws Exception {
        // Upload test
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.mp3", 
            "audio/mpeg", 
            "test audio content".getBytes()
        );

        mockMvc.perform(multipart("/api/audio/123").file(file))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.logId").value(123));

        // Get test
        mockMvc.perform(get("/api/audio/123"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.logId").value(123));
    }
}