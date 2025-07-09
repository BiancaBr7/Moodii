package com.moodii.repository;

import com.moodii.model.Mood;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface MoodRepository extends MongoRepository<Mood, String> {
    Optional<Mood> findByTypeIgnoreCase(String type);
    Optional<Mood> findByMoodId(Integer moodId);
}