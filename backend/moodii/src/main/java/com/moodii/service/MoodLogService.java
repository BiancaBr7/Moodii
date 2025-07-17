package com.moodii.service;

import com.moodii.model.MoodLog;
import com.moodii.repository.MoodLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MoodLogService {
    private final MoodLogRepository moodLogRepository;

    public MoodLogService(MoodLogRepository moodLogRepository) {
        this.moodLogRepository = moodLogRepository;
    }

    public List<MoodLog> getMoodLogsByUser(Integer userId) {
        return moodLogRepository.findByUserId(userId);
    }

    public MoodLog createMoodLog(MoodLog moodLog) {
        return moodLogRepository.save(moodLog);
    }

    public MoodLog getMoodLogById(Integer logId) {
        return moodLogRepository.findByLogId(logId);
    }
}