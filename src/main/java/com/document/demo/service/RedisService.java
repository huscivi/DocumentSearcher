package com.document.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void saveEmbedding(String documentId, List<Double> embedding) {
        try {
            String json = objectMapper.writeValueAsString(embedding);
            redisTemplate.opsForValue().set("embedding:" + documentId, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to save embedding to Redis", e);
        }
    }

    public List<Double> getEmbedding(String documentId) {
        String json = redisTemplate.opsForValue().get("embedding:" + documentId);
        if (json == null) return null;

        try {
            return objectMapper.readValue(json, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read embedding from Redis", e);
        }
    }

    public Map<String, List<Double>> getAllEmbeddings() {
        Map<String, List<Double>> embeddings = new HashMap<>();

        Set<String> keys = redisTemplate.keys("embedding:*");
        if (keys != null) {
            for (String key : keys) {
                String json = redisTemplate.opsForValue().get(key);
                if (json != null) {
                    try {
                        List<Double> embedding = objectMapper.readValue(json, List.class);
                        String documentId = key.replace("embedding:", "");
                        embeddings.put(documentId, embedding);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to parse embedding from Redis", e);
                    }
                }
            }
        }

        return embeddings;
    }

}
