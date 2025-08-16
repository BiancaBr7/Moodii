package com.moodii;

// import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.moodii.model.Mood;
import com.moodii.repository.MoodRepository;

@SpringBootApplication
public class MoodiiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoodiiApplication.class, args);
	}

	@Bean
    CommandLineRunner initMoods(MoodRepository moodRepo) {
        return args -> {
            if (moodRepo.count() == 0) {
                List<Mood> defaultMoods = List.of(
                    new Mood(1, "Happy", "😊"),
                    new Mood(2, "Sad", "😢"),
                    new Mood(3, "Angry", "😠"),
                    new Mood(4, "Fearful", "😨"),
                    new Mood(5, "Neutral", "😐")
                );
                moodRepo.saveAll(defaultMoods);
                System.out.println("✅ Default moods initialized!");
            }
        };
    }
}
