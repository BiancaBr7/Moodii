package com.moodii.controller;

import com.moodii.model.Mood;
import com.moodii.service.MoodService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import java.util.List;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/moods")
public class MoodController {
    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @GetMapping("/{moodId}")
    public ResponseEntity<Mood> getMoodById(@PathVariable Integer moodId) {
        Mood mood = moodService.getMoodById(moodId);
        if (mood != null) {
            return ResponseEntity.ok(mood);  // HTTP 200 with mood data
        } else {
            return ResponseEntity.notFound().build();  // HTTP 404
        }
    }
}