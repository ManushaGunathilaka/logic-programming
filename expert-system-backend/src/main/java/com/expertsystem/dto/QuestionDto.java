package com.expertsystem.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String symptomName;
    private String question;
    private Integer questionNumber;
    private Integer totalQuestions;

    public QuestionDto(String symptomName, String question, Integer questionNumber, Integer totalQuestions) {
        this.symptomName = symptomName;
        this.question = question;
        this.questionNumber = questionNumber;
        this.totalQuestions = totalQuestions;
    }
}