package com.moodii.repository;

import com.moodii.model.Mood;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MoodRepository extends MongoRepository<Mood, String> {
    // List<Mood> findAll();
    @Query("{'moodId': ?0}")
    Mood findOneByMoodId(Integer moodId);
}