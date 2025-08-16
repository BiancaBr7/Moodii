package com.moodii.service;

import com.moodii.dto.MlPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MlClientService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public MlPredictionResponse predictEmotion(MultipartFile audioFile) throws IOException {
        String url = mlServiceUrl.endsWith("/") ? mlServiceUrl + "predict" : mlServiceUrl + "/predict";
        org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();

        org.springframework.core.io.ByteArrayResource resource = new org.springframework.core.io.ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() { return audioFile.getOriginalFilename(); }
        };
        body.add("audio", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        MlPredictionResponse responseDto = new MlPredictionResponse();
        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity) restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            Map<String, Object> map = response.getBody();
            if(response.getStatusCode().is2xxSuccessful() && map!=null){
                responseDto.setStatus((String) map.getOrDefault("status", "success"));
                Object pred = map.get("predicted_emotion");
                if(pred instanceof String) responseDto.setPredictedEmotion((String) pred);
                Object conf = map.get("confidence");
                if(conf instanceof Number) responseDto.setConfidence(((Number) conf).doubleValue());
                Object reqId = map.get("request_id");
                if(reqId instanceof String) responseDto.setRequestId((String) reqId);
                Map<String, Double> probs = new HashMap<>();
                Object all = map.get("all_predictions");
                if(all instanceof Map<?,?> ap){
                    for(Map.Entry<?,?> e : ap.entrySet()){
                        if(e.getKey()!=null && e.getValue() instanceof Number n){
                            probs.put(e.getKey().toString(), n.doubleValue());
                        }
                    }
                }
                responseDto.setAllPredictions(probs);
            } else {
                responseDto.setStatus("error");
                responseDto.setError("ML service non-2xx or empty body: "+response.getStatusCode());
            }
        } catch (RestClientException ex){
            responseDto.setStatus("error");
            responseDto.setError("ML request failed: "+ex.getMessage());
        }
        return responseDto;
    }
}
