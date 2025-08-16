package com.moodii.repository;

import com.moodii.model.MoodLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodLogRepository extends MongoRepository<MoodLog, String> {
    
    // Find by userId
    List<MoodLog> findByUserId(Integer userId);
    
    // Find by date (using LocalDateTime for createdAt field)
    @Query("{'userId': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    List<MoodLog> findByUserIdAndCreatedAtBetween(Integer userId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by specific date (same day)
    @Query("{'userId': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    List<MoodLog> findByUserIdAndDate(Integer userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    // Find by month (for calendar view)
    @Query("{'userId': ?0, 'createdAt': {$gte: ?1, $lt: ?2}}")
    List<MoodLog> findByUserIdAndMonth(Integer userId, LocalDateTime startOfMonth, LocalDateTime endOfMonth);
    
    // Find by id and userId (for security)
    Optional<MoodLog> findByIdAndUserId(String id, Integer userId);
    
    // Delete by id and userId (for security)
    void deleteByIdAndUserId(String id, Integer userId);
    
    // Check if exists by id and userId (for security)
    boolean existsByIdAndUserId(String id, Integer userId);
}
