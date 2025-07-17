package com.moodii.controller;

import com.moodii.model.MoodLog;
import com.moodii.service.MoodLogService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mood-logs")
public class MoodLogController {
    private final MoodLogService moodLogService;

    public MoodLogController(MoodLogService moodLogService) {
        this.moodLogService = moodLogService;
    }

    @GetMapping("/user/{userId}")
    public List<MoodLog> getMoodLogsByUser(@PathVariable Integer userId) {
        return moodLogService.getMoodLogsByUser(userId);
    }

    @PostMapping
    public MoodLog createMoodLog(@RequestBody MoodLog moodLog) {
        return moodLogService.createMoodLog(moodLog);
    }

    @GetMapping("/{logId}")
    public MoodLog getMoodLogById(@PathVariable Integer logId) {
        return moodLogService.getMoodLogById(logId);
    }
}