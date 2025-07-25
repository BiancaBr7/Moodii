package com.moodii.service;

import com.moodii.dto.MoodLogRequest;
import com.moodii.dto.MoodLogResponse;
import com.moodii.model.MoodLog;
import com.moodii.repository.MoodLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodLogService {
    
    private final MoodLogRepository moodLogRepository;

    public MoodLogResponse createMoodLog(MoodLogRequest request) {
        MoodLog moodLog = new MoodLog();
        moodLog.setTitle(request.getTitle());
        moodLog.setTranscription(request.getTranscription());
        moodLog.setMoodType(request.getMoodType());
        moodLog.setUserId(request.getUserId());
        moodLog.setCreatedAt(LocalDateTime.now());
        
        MoodLog savedMoodLog = moodLogRepository.save(moodLog);
        return convertToResponse(savedMoodLog);
    }

    public Optional<MoodLogResponse> updateMoodLogMood(String id, Integer userId, Integer newMood) {
        Optional<MoodLog> moodLogOpt = moodLogRepository.findByIdAndUserId(id, userId);
        if (moodLogOpt.isPresent()) {
            MoodLog moodLog = moodLogOpt.get();
            moodLog.setMoodType(newMood);
            MoodLog updatedMoodLog = moodLogRepository.save(moodLog);
            return Optional.of(convertToResponse(updatedMoodLog));
        }
        return Optional.empty();
    }

    public List<MoodLogResponse> getMoodLogsByDate(Integer userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<MoodLog> moodLogs = moodLogRepository.findByUserIdAndDate(userId, startOfDay, endOfDay);
        return moodLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<MoodLogResponse> getMoodLogsByMonth(Integer userId, YearMonth month) {
        LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = month.plusMonths(1).atDay(1).atStartOfDay();
        
        List<MoodLog> moodLogs = moodLogRepository.findByUserIdAndMonth(userId, startOfMonth, endOfMonth);
        return moodLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<MoodLogResponse> getMoodLogById(String id, Integer userId) {
        Optional<MoodLog> moodLogOpt = moodLogRepository.findByIdAndUserId(id, userId);
        return moodLogOpt.map(this::convertToResponse);
    }

    public boolean deleteMoodLog(String id, Integer userId) {
        if (moodLogRepository.existsByIdAndUserId(id, userId)) {
            moodLogRepository.deleteByIdAndUserId(id, userId);
            return true;
        }
        return false;
    }

    public List<MoodLogResponse> getMoodLogsByUser(Integer userId) {
        List<MoodLog> moodLogs = moodLogRepository.findByUserId(userId);
        return moodLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private MoodLogResponse convertToResponse(MoodLog moodLog) {
        MoodLogResponse response = new MoodLogResponse();
        response.setId(moodLog.getId());
        response.setTitle(moodLog.getTitle());
        response.setTranscription(moodLog.getTranscription());
        response.setMoodType(moodLog.getMoodType());
        response.setUserId(moodLog.getUserId());
        response.setCreatedAt(moodLog.getCreatedAt());
        return response;
    }
}