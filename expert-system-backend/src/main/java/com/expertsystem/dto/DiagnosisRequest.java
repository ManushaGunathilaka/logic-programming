package com.expertsystem.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DiagnosisRequest {
    private Map<String, Boolean> symptoms;
}