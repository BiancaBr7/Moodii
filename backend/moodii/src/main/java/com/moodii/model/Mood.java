package com.moodii.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter 
@Entity
@Table(name = "moods")
public class Mood {
    @Id
    private String id;
    private Integer moodId;
    private String type;
    private String emoji;
    private String description;
}