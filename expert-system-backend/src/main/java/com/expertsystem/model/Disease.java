package com.expertsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class Disease {
    private String name;
    private String description;
    private String treatment;
    private List<String> symptoms;
}