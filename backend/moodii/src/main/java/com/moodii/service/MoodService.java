package com.moodii.service;

import com.moodii.model.Mood;
import com.moodii.repository.MoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MoodService {
    private final MoodRepository moodRepository;

    public MoodService(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    public List<Mood> getAllMoods() {
        return moodRepository.findAll();
    }

    public Mood getMoodById(Integer moodId) {
        return moodRepository.findOneByMoodId(moodId);
    }
}