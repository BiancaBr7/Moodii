package com.moodii.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class MoodLogResponse {
    private String id;
    private String title;
    private String transcription;
    private Integer moodType;
    private Integer userId;
    private LocalDateTime createdAt;
}
