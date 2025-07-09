/**
 * AudioControllerTest tests the basic Audio API. To run tests, run "mvnw test"
 * within the terminal. 
 */

package com.moodii.controller;

import com.moodii.model.Audio;
import com.moodii.repository.AudioFileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AudioFileRepository audioRepository;

    @AfterEach
    void tearDown() {
        audioRepository.deleteAll();
    }

    @Test
    /* Testing POST/audio/upload */
    void shouldUploadAudio() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.mp3",
            "audio/mpeg",
            "test audio content".getBytes()
        );

        mockMvc.perform(multipart("/audio/upload")
               .file(file)
               .param("log_id", "1")
               .contentType(MediaType.MULTIPART_FORM_DATA))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.logId").value(1))
               .andExpect(jsonPath("$.format").value("audio/mpeg"));
    }

    @Test
    /* Testing GET/audio/{id} */
    void shouldGetAudioMetadata() throws Exception {
        Audio audio = new Audio();
        audio.setLogId(1);
        audio.setFormat("audio/mpeg");
        audio = audioRepository.save(audio);

        mockMvc.perform(get("/audio/" + audio.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").exists())
               .andExpect(jsonPath("$.logId").value(1));
    }

    @Test
    /* Testing DELETE/audio/{id} */
    void shouldDeleteAudio() throws Exception {
        Audio audio = audioRepository.save(new Audio());

        mockMvc.perform(delete("/audio/" + audio.getId()))
               .andExpect(status().isNoContent());

        mockMvc.perform(get("/audio/" + audio.getId()))
               .andExpect(status().isNotFound());
    }
}