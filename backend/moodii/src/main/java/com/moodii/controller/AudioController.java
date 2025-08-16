package com.moodii.controller;

import com.moodii.model.AudioFile;
import com.moodii.service.AudioFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/audio")
public class AudioController {
    private final AudioFileService audioFileService;

    public AudioController(AudioFileService audioFileService) {
        this.audioFileService = audioFileService;
    }

    @PostMapping("/{logId}")
    public AudioFile uploadAudio(
            @PathVariable String logId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return audioFileService.saveAudioFile(logId, file);
    }

    @PostMapping("/upload")
    public AudioFile uploadAudioWithParam(
            @RequestParam("logId") String logId,
            @RequestParam("audio") MultipartFile file) throws IOException {
        return audioFileService.saveAudioFile(logId, file);
    }

    @GetMapping("/{logId}")
    public ResponseEntity<byte[]> getAudioFile(@PathVariable String logId) {
        Optional<AudioFile> audioFileOpt = audioFileService.getAudioFileByLogId(logId);
        
        if (audioFileOpt.isPresent()) {
            AudioFile audioFile = audioFileOpt.get();
            
            // Determine content type based on format
            String contentType = "audio/mpeg"; // Default to MP3
            if (audioFile.getFormat() != null) {
                String format = audioFile.getFormat().toLowerCase();
                // Handle both MIME types and file extensions
                if (format.contains("webm") || format.equals("webm")) {
                    contentType = "audio/webm";
                } else if (format.contains("wav") || format.equals("wav")) {
                    contentType = "audio/wav";
                } else if (format.contains("mp3") || format.equals("mp3") || format.contains("mpeg")) {
                    contentType = "audio/mpeg";
                } else if (format.contains("m4a") || format.equals("m4a") || format.contains("mp4")) {
                    contentType = "audio/mp4";
                } else if (format.contains("ogg") || format.equals("ogg")) {
                    contentType = "audio/ogg";
                } else if (format.contains("opus")) {
                    contentType = "audio/opus";
                } else {
                    // If it's already a full MIME type, use it as is
                    contentType = audioFile.getFormat();
                }
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(audioFile.getAudioBlob().length);
            
            // Extract file extension from content type for filename
            String fileExtension = "mp3"; // default
            if (contentType.contains("webm")) fileExtension = "webm";
            else if (contentType.contains("wav")) fileExtension = "wav";
            else if (contentType.contains("ogg")) fileExtension = "ogg";
            else if (contentType.contains("m4a") || contentType.contains("mp4")) fileExtension = "m4a";
            
            headers.set("Content-Disposition", "inline; filename=\"audio_" + logId + "." + fileExtension + "\"");
            
            return new ResponseEntity<>(audioFile.getAudioBlob(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/metadata/{logId}")
    public ResponseEntity<AudioFile> getAudioFileMetadata(@PathVariable String logId) {
        Optional<AudioFile> audioFileOpt = audioFileService.getAudioFileByLogId(logId);
        
        if (audioFileOpt.isPresent()) {
            // Return metadata without the binary blob for performance
            AudioFile metadata = audioFileOpt.get();
            AudioFile response = new AudioFile();
            response.setId(metadata.getId());
            response.setFileId(metadata.getFileId());
            response.setLogId(metadata.getLogId());
            response.setFormat(metadata.getFormat());
            // Don't include audioBlob in metadata response
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}