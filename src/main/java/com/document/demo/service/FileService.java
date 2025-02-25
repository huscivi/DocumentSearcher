package com.document.demo.service;

import com.document.demo.model.AnswerResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private AIService aiService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private RedisService redisService;

    private final Tika tika = new Tika();

    public DocumentResponse summarizeDocument(String documentId) {
        String content = redisTemplate.opsForValue().get("document:" + documentId);
        if (content == null) throw new RuntimeException("Document not found in Redis");

        Optional<DocumentMetadata> metadata = documentRepository.findById(documentId);
        if (metadata.isEmpty()) throw new RuntimeException("Document metadata not found");

        return aiService.summarizeDocument(content, metadata.get().getTitle());
    }

    public void processFile(MultipartFile file) {
        try {
            String content = tika.parseToString(file.getInputStream());

            DocumentMetadata metadata = new DocumentMetadata();
            metadata.setTitle(file.getOriginalFilename());
            metadata.setType(file.getContentType());
            metadata.setUploadDate(LocalDate.now());
            documentRepository.save(metadata);

            redisTemplate.opsForValue().set("document:" + metadata.getId(), content);

            List<Double> embedding = embeddingService.getEmbedding(content);
            redisService.saveEmbedding(metadata.getId(), embedding);

        } catch (IOException | TikaException e) {
            throw new RuntimeException("Failed to process file", e);
        }
    }

    public AnswerResponse answerQuestion(String question) {
        Map<String, List<Double>> allEmbeddings = redisService.getAllEmbeddings();
        if (allEmbeddings.isEmpty()) throw new RuntimeException("No embeddings found");

        List<Double> questionEmbedding = embeddingService.getEmbedding(question);

        String bestDocumentId = null;
        double bestSimilarity = 0.0;

        for (Map.Entry<String, List<Double>> entry : allEmbeddings.entrySet()) {
            double similarity = calculateCosineSimilarity(questionEmbedding, entry.getValue());
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestDocumentId = entry.getKey();
            }
        }

        if (bestSimilarity < 0.7) return null;

        String content = redisTemplate.opsForValue().get("document:" + bestDocumentId);
        if (content == null) throw new RuntimeException("Document content not found");

        return aiService.getAnswerAsObject(question, content);
    }

    private double calculateCosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            double valA = vectorA.get(i);
            double valB = vectorB.get(i);
            dotProduct += valA * valB;
            normA += valA * valA;
            normB += valB * valB;
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public List<DocumentMetadata> listAllDocuments() {
        return documentRepository.findAll();
    }

}
