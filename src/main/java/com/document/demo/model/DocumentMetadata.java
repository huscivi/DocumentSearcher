package com.document.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "documents")
public class DocumentMetadata {

    @Id
    private String id;
    private String title;
    private String type;
    private LocalDate uploadDate;
    private String uploadedBy;
}
