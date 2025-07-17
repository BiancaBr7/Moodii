package com.moodii.repository;

import com.moodii.model.AudioFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Testcontainers
public class AudioFileRepositoryIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private AudioFileRepository audioFileRepository;

    @BeforeEach
    void setUp() {
        audioFileRepository.deleteAll();
    }

    @Test
    public void findByLogId_ReturnsAudioFile_WhenExists() {
        // Arrange
        AudioFile audioFile = new AudioFile(null, 1, 123, "test".getBytes(), "mp3");
        audioFileRepository.save(audioFile);

        // Act
        AudioFile found = audioFileRepository.findByLogId(123);

        // Assert
        assertNotNull(found);
        assertEquals(123, found.getLogId());
        assertEquals("mp3", found.getFormat());
    }
}