package com.moodii.repository;

import com.moodii.model.MoodLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MoodLogRepository extends MongoRepository<MoodLog, String> {
    List<MoodLog> findByUserId(Integer userId);
    MoodLog findByLogId(Integer logId);
}