package com.moodii.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.moodii.model.Audio;

public interface AudioFileRepository extends MongoRepository<Audio, Integer> {
}