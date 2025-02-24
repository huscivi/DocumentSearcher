package com.document.demo.service;

import com.document.demo.model.DocumentResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIService {

    private final OpenAiChatModel chatModel;

    @Autowired
    public AIService(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public DocumentResponse summarizeDocument(String content, String title) {
        String prompt = "Summarize the following document titled '" + title + "' and list key points:\n" + content;
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return new DocumentResponse(title, response.getResult().getOutput().getText(), List.of());
    }

    public String answerQuestion(String question, String content) {
        String prompt = "Based on the following document, answer the question:\n" + content + "\nQuestion: " + question;
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return response.getResult().getOutput().getText();
    }
}
