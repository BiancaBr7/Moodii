package com.moodii.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Document(collection = "mood_logs")
public class MoodLog {
    @Id
    private String id;
    private Integer logId;
    private String title;
    private String transcription;
    private Integer moodType;
    private Integer userId;
    private LocalDateTime createdAt;
}