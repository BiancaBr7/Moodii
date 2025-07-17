package com.moodii.repository;

import com.moodii.model.AudioFile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AudioFileRepository extends MongoRepository<AudioFile, String> {
    AudioFile findByLogId(Integer logId);
}