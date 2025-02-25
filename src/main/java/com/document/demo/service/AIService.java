package com.document.demo.service;

import com.document.demo.model.AnswerResponse;
import com.document.demo.model.DocumentResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
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

    public AnswerResponse getAnswerAsObject(String question, String content) {
        var converter = new BeanOutputConverter(AnswerResponse.class);
        String format = converter.getFormat();
        String prompt = "Answer the following question based on this document:\n" + content + "\nQuestion: " + question + format;
        Prompt aiPrompt = new Prompt(prompt);

        ChatResponse response = chatModel.call(aiPrompt);
        Generation result = response.getResult();

        return (AnswerResponse) converter.convert(result.getOutput().getText());
    }

    public DocumentResponse summarizeDocument(String content, String title) {
        String prompt = "Summarize the following document titled '" + title + "' and list key points:\n" + content;
        ChatResponse response = chatModel.call(new Prompt(prompt));
        return new DocumentResponse(title, response.getResult().getOutput().getText(), List.of());
    }

}
