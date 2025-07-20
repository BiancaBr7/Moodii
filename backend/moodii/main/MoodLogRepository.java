package com.example.moodtracker.repository;

import com.example.moodtracker.model.MoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoodLogRepository extends JpaRepository<MoodLog, Long> {
    List<MoodLog> findByDate(LocalDate date);
    List<MoodLog> findByDateBetween(LocalDate start, LocalDate end);
}