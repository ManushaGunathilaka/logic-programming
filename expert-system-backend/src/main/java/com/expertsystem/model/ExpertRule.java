package com.expertsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpertRule {
    private String ruleName;
    private String condition;
    private String conclusion;
    private double confidence;
    // Priority field removed
}