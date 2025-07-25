package com.moodii.controller;

import com.moodii.dto.MoodLogRequest;
import com.moodii.dto.MoodLogResponse;
import com.moodii.service.MoodLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MoodLogController {

    private final MoodLogService moodLogService;

    // POST /api/moodlogs - Create a new mood log
    @PostMapping("/moodlogs")
    public ResponseEntity<MoodLogResponse> createMoodLog(@RequestBody MoodLogRequest request) {
        try {
            MoodLogResponse response = moodLogService.createMoodLog(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/moodlog?mood=xxx - Update mood of existing log
    @PostMapping("/moodlog")
    public ResponseEntity<MoodLogResponse> updateMoodLogMood(
            @RequestParam("mood") Integer mood,
            @RequestParam("id") String id,
            @RequestParam("userId") Integer userId) {
        try {
            Optional<MoodLogResponse> updated = moodLogService.updateMoodLogMood(id, userId, mood);
            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/moodlogs?date=YYYY-MM-DD - Get mood logs for specific date
    @GetMapping("/moodlogs")
    public ResponseEntity<List<MoodLogResponse>> getMoodLogsByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("userId") Integer userId) {
        try {
            List<MoodLogResponse> moodLogs = moodLogService.getMoodLogsByDate(userId, date);
            return ResponseEntity.ok(moodLogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/moodlogs/calendar?month=YYYY-MM - Get mood logs for specific month
    @GetMapping("/moodlogs/calendar")
    public ResponseEntity<List<MoodLogResponse>> getMoodLogsByMonth(
            @RequestParam("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam("userId") Integer userId) {
        try {
            List<MoodLogResponse> moodLogs = moodLogService.getMoodLogsByMonth(userId, month);
            return ResponseEntity.ok(moodLogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/moodlogs/{id} - Get specific mood log by ID
    @GetMapping("/moodlogs/{id}")
    public ResponseEntity<MoodLogResponse> getMoodLogById(
            @PathVariable String id,
            @RequestParam("userId") Integer userId) {
        try {
            Optional<MoodLogResponse> moodLog = moodLogService.getMoodLogById(id, userId);
            if (moodLog.isPresent()) {
                return ResponseEntity.ok(moodLog.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/moodlogs/{id} - Delete specific mood log by ID
    @DeleteMapping("/moodlogs/{id}")
    public ResponseEntity<Void> deleteMoodLog(
            @PathVariable String id,
            @RequestParam("userId") Integer userId) {
        try {
            boolean deleted = moodLogService.deleteMoodLog(id, userId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Bonus: GET all mood logs for a user
    @GetMapping("/users/{userId}/moodlogs")
    public ResponseEntity<List<MoodLogResponse>> getAllMoodLogsByUser(@PathVariable Integer userId) {
        try {
            List<MoodLogResponse> moodLogs = moodLogService.getMoodLogsByUser(userId);
            return ResponseEntity.ok(moodLogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
