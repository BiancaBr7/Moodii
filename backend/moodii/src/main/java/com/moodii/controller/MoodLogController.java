package com.example.moodtracker.controller;

import com.example.moodtracker.model.MoodLog;
import com.example.moodtracker.repository.MoodLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/moodlogs")
@RequiredArgsConstructor
public class MoodLogController {

    private final MoodLogRepository moodLogRepository;

    @PostMapping
    public ResponseEntity<MoodLog> createMoodLog(@RequestBody MoodLog moodLog) {
        return ResponseEntity.ok(moodLogRepository.save(moodLog));
    }

    @GetMapping
    public ResponseEntity<List<MoodLog>> getMoodLogsByDate(@RequestParam("date")
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(moodLogRepository.findByDate(date));
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<MoodLog>> getMoodLogsByMonth(@RequestParam("month")
                @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();
        return ResponseEntity.ok(moodLogRepository.findByDateBetween(start, end));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoodLog> getMoodLogById(@PathVariable Long id) {
        return moodLogRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoodLog(@PathVariable Long id) {
        if (!moodLogRepository.existsById(id)) return ResponseEntity.notFound().build();
        moodLogRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
