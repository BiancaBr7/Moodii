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
 * - string id (MongoDB ObjectId)
 * - integer fileId
 * - string logId (References MoodLog.id)
 * - binary audioBlob
 * - string format
 */
public class AudioFile {
    @Id
    private String id;
    private Integer fileId;
    private String logId; // Changed to String to match MoodLog.id
    private byte[] audioBlob;
    private String format;
}