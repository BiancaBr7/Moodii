package com.moodii.repository;

import com.moodii.model.AudioFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AudioFileRepository extends MongoRepository<AudioFile, String> {
    Optional<AudioFile> findByLogId(String logId); // Changed to String and Optional for better handling
}