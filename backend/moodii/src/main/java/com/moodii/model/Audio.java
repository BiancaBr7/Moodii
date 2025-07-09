package com.moodii.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audio_files")

/*
 * Represents an audio in the Moodii application
 * Audio:
 * - integer id
 * - integer logId
 * - binary audioBlob
 * - string format
 */
public class Audio {
    @Id
    private String id;

    private Integer logId;
    private byte[] audioBlob;
    private String format;
}