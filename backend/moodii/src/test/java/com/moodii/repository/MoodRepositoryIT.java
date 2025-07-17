package com.moodii.repository;

import com.moodii.model.Mood;
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
public class MoodRepositoryIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MoodRepository moodRepository;

    @BeforeEach
    void setUp() {
        moodRepository.deleteAll(); // Clear the collection before each test
    }

    @Test
    public void saveMood_ReturnsSavedMood() {
        Mood mood = new Mood(1, "Happy", "😊");
        Mood saved = moodRepository.save(mood);
        
        assertNotNull(saved.getId());
        assertEquals("Happy", saved.getType());
        assertEquals(1, saved.getMoodId());
    }
}