package com.expertsystem.controller;

import com.expertsystem.dto.DiagnosisRequest;
import com.expertsystem.dto.DiagnosisResult;
import com.expertsystem.dto.QuestionDto;
import com.expertsystem.service.ExpertSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expert-system")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpertSystemController {

    @Autowired
    private ExpertSystemService expertSystemService;

    @PostMapping("/diagnose")
    public ResponseEntity<DiagnosisResult> diagnose(@RequestBody DiagnosisRequest request) {
        System.out.println("📥 Received diagnosis request with " + request.getSymptoms().size() + " symptoms");
        DiagnosisResult result = expertSystemService.diagnose(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDto>> getQuestions() {
        List<QuestionDto> questions = expertSystemService.getQuestions();
        System.out.println("📋 Sending " + questions.size() + " questions to frontend");
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ Expert System is running with In-Memory Knowledge Base!");
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> info = expertSystemService.getSystemInfo();
        return ResponseEntity.ok(info);
    }
}