package com.moodii.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MoodLogRequest {
    private String title;
    private String transcription;
    private Integer moodType;
    private Integer userId;
}
