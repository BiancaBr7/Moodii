package com.moodii.controller;

import com.moodii.model.Mood;
import com.moodii.repository.MoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class MoodControllerIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private MoodRepository moodRepository;

    @BeforeEach
    void setup() {
        moodRepository.deleteAll();
        Mood testMood = new Mood(1, "Happy", "😊");
        moodRepository.save(testMood);
        assertEquals(1, moodRepository.count());
    }

    @Test
    public void getMoodById_Returns200_WhenFound() throws Exception {
        mockMvc.perform(get("/api/moods/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.type").value("Happy"));
    }

    @Test
    public void getMoodById_Returns404_WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/moods/999"))
               .andExpect(status().isNotFound());
    }
}