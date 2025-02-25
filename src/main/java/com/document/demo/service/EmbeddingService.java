package com.document.demo.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    public List<Double> getEmbedding(String text) {
        float[] embeddingArray = embeddingModel.embed(text);

        List<Double> embeddingList = new ArrayList<>();
        for (double value : embeddingArray) {
            embeddingList.add(value);
        }

        return embeddingList;
    }
}
