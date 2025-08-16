package com.moodii.service;

import com.moodii.model.Mood;
import com.moodii.repository.MoodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MoodServiceTest {

    @Mock
    private MoodRepository moodRepository;

    @InjectMocks
    private MoodService moodService;

    @Test
    public void getAllMoods_ReturnsAllMoods() {
        // Arrange
        when(moodRepository.findAll()).thenReturn(List.of(
            new Mood(1, "Happy", "😊")
        ));

        // Act
        List<Mood> result = moodService.getAllMoods();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Happy", result.get(0).getType());
    }
}