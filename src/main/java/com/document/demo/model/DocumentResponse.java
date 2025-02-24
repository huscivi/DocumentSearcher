package com.document.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String documentTitle;
    private String summary;
    private List<String> keyPoints;
}
