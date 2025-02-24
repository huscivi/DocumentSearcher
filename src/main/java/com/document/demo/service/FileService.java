package com.document.demo.service;

import com.document.demo.model.DocumentMetadata;
import com.document.demo.model.DocumentResponse;
import com.document.demo.repository.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private AIService aiService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final Tika tika = new Tika();

    public void processFile(MultipartFile file) {
        try {
            String content = tika.parseToString(file.getInputStream());

            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setTitle(file.getOriginalFilename());
            metadata.setType(file.getContentType());
            metadata.setUploadDate(LocalDate.now());
            metadata.setUploadedBy("user@example.com");
            documentRepository.save(metadata);

            redisTemplate.opsForValue().set("document:" + metadata.getId(), content);

        } catch (IOException | TikaException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public DocumentResponse summarizeDocument(String documentId) {
        String content = redisTemplate.opsForValue().get("document:" + documentId);
        if (content == null) throw new RuntimeException("Document not found in Redis");

        Optional<DocumentMetadata> metadata = documentRepository.findById(documentId);
        if (metadata.isEmpty()) throw new RuntimeException("Document metadata not found");

        return aiService.summarizeDocument(content, metadata.get().getTitle());
    }

    public String answerQuestion(String documentId, String question) {
        String content = redisTemplate.opsForValue().get("document:" + documentId);
        if (content == null) throw new RuntimeException("Document not found in Redis");

        return aiService.answerQuestion(question, content);
    }
}
