package com.document.demo.controller;

import com.document.demo.model.DocumentResponse;
import com.document.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        fileService.processFile(file);
        return ResponseEntity.ok("File uploaded successfully!");
    }

    @GetMapping("/summarize/{documentId}")
    public ResponseEntity<DocumentResponse> summarizeDocument(@PathVariable String documentId) {
        DocumentResponse response = fileService.summarizeDocument(documentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ask/{documentId}")
    public ResponseEntity<String> askQuestion(@PathVariable String documentId, @RequestParam String question) {
        String answer = fileService.answerQuestion(documentId, question);
        return ResponseEntity.ok(answer);
    }
}
