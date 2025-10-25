package com.expertsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class DiagnosisResult {
    private String disease;
    private String description;
    private String treatment;
    private Double confidence;
    private List<String> matchedSymptoms;
    private String recommendation;

    public DiagnosisResult(String disease, String description, String treatment,
                           Double confidence, List<String> matchedSymptoms, String recommendation) {
        this.disease = disease;
        this.description = description;
        this.treatment = treatment;
        this.confidence = confidence;
        this.matchedSymptoms = matchedSymptoms;
        this.recommendation = recommendation;
    }
}