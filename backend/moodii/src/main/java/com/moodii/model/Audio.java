/**
 * User.java
 *
 * Represents a user entity for the Moodii application.
 * This class is mapped to the 'users' collection in MongoDB and stores user credentials and roles.
 *
 * @author [Alyssa Dong]
 * @since 2025-07-09
 */

package com.moodii.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor


@Entity
@Table(name = "audio_files")
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "log_id")
    private Integer logId;

    @Lob
    @Column(name = "audio_blob")
    private byte[] audioBlob;

    private String format;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    
    public byte[] getAudioBlob() { return audioBlob; }
    public void setAudioBlob(byte[] audioBlob) { this.audioBlob = audioBlob; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
