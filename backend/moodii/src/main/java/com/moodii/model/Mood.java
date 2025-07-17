package com.moodii.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter 
@Document(collection = "moods") // MongoDB annotation instead of @Entity
public class Mood {
    @Id
    private String id;
    
    @Indexed(unique = true) // Ensures moodId is unique
    private Integer moodId;
    
    private String type;
    private String emoji;

    public Mood() {} // Required no-arg constructor

    public Mood(Integer moodId, String type, String emoji) {
        this.moodId = moodId;
        this.type = type;
        this.emoji = emoji;
    }
}