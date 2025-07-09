package com.moodii.repository;

import com.moodii.model.Audio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioFileRepository extends JpaRepository<Audio, Integer> {
}