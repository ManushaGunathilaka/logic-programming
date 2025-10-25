package com.expertsystem.service;

import com.expertsystem.model.Disease;
import com.expertsystem.model.ExpertRule;
import com.expertsystem.model.Symptom;
import com.expertsystem.dto.DiagnosisRequest;
import com.expertsystem.dto.DiagnosisResult;
import com.expertsystem.dto.QuestionDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpertSystemService {

    private final List<Symptom> symptoms;
    private final List<Disease> diseases;
    private final List<ExpertRule> expertRules;

    public ExpertSystemService() {
        // Initialize symptoms
        this.symptoms = Arrays.asList(
                new Symptom("fever", "Do you have a fever?"),
                new Symptom("cough", "Do you have a persistent cough?"),
                new Symptom("headache", "Are you experiencing headaches?"),
                new Symptom("fatigue", "Do you feel unusually tired?"),
                new Symptom("sore_throat", "Do you have a sore throat?"),
                new Symptom("runny_nose", "Do you have a runny nose?"),
                new Symptom("body_aches", "Do you have body aches?"),
                new Symptom("nausea", "Are you feeling nauseous?"),
                new Symptom("vomiting", "Have you been vomiting?"),
                new Symptom("diarrhea", "Do you have diarrhea?"),
                new Symptom("chest_pain", "Are you experiencing chest pain?"),
                new Symptom("shortness_of_breath", "Do you have shortness of breath?")
        );

        // Initialize diseases with their symptoms
        this.diseases = Arrays.asList(
                new Disease("Common Cold",
                        "Viral infection of the nose and throat",
                        "Rest, fluids, over-the-counter cold medicine",
                        Arrays.asList("fever", "cough", "headache", "runny_nose", "sore_throat")),

                new Disease("Flu (Influenza)",
                        "Respiratory illness caused by influenza viruses",
                        "Rest, fluids, antiviral medication if early",
                        Arrays.asList("fever", "cough", "headache", "fatigue", "body_aches")),

                new Disease("COVID-19",
                        "Respiratory illness caused by coronavirus",
                        "Isolation, rest, medical consultation",
                        Arrays.asList("fever", "cough", "fatigue", "chest_pain", "shortness_of_breath")),

                new Disease("Stomach Flu",
                        "Viral infection causing stomach inflammation",
                        "Hydration, bland diet, rest",
                        Arrays.asList("nausea", "vomiting", "diarrhea", "fever")),

                new Disease("Strep Throat",
                        "Bacterial throat infection",
                        "Antibiotics, rest, pain relief",
                        Arrays.asList("fever", "sore_throat", "headache"))
        );

        // Initialize expert rules (no priority field)
        this.expertRules = Arrays.asList(
                new ExpertRule("COVID Rule", "fever && cough && shortness_of_breath && fatigue", "COVID-19", 0.90),
                new ExpertRule("Flu Rule", "fever && cough && body_aches && fatigue", "Flu (Influenza)", 0.85),
                new ExpertRule("Cold Rule", "runny_nose && sore_throat && cough", "Common Cold", 0.80),
                new ExpertRule("Stomach Flu Rule", "nausea && vomiting && diarrhea", "Stomach Flu", 0.75),
                new ExpertRule("Strep Throat Rule", "sore_throat && fever && headache", "Strep Throat", 0.70)
        );

        System.out.println("✅ Knowledge Base Initialized:");
        System.out.println("   - " + symptoms.size() + " symptoms loaded");
        System.out.println("   - " + diseases.size() + " diseases loaded");
        System.out.println("   - " + expertRules.size() + " expert rules loaded");
    }

    public DiagnosisResult diagnose(DiagnosisRequest request) {
        List<String> presentSymptoms = request.getSymptoms().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        System.out.println("🔍 Diagnosing with symptoms: " + presentSymptoms);

        if (presentSymptoms.isEmpty()) {
            return createNoSymptomsResult();
        }

        // Rule-based diagnosis
        List<ExpertRule> matchedRules = evaluateRules(presentSymptoms);

        // Symptom matching with diseases
        List<Disease> possibleDiseases = findPossibleDiseases(presentSymptoms);

        // Combine results
        DiagnosisResult result = combineDiagnoses(matchedRules, possibleDiseases, presentSymptoms);

        System.out.println("🎯 Diagnosis Result: " + result.getDisease() + " (" + result.getConfidence() + "% confidence)");

        return result;
    }

    private List<ExpertRule> evaluateRules(List<String> presentSymptoms) {
        List<ExpertRule> matchedRules = new ArrayList<>();

        for (ExpertRule rule : expertRules) {
            if (matchesRule(rule, presentSymptoms)) {
                matchedRules.add(rule);
            }
        }

        // Sort by confidence (highest first)
        matchedRules.sort((r1, r2) -> Double.compare(r2.getConfidence(), r1.getConfidence()));

        return matchedRules;
    }

    private boolean matchesRule(ExpertRule rule, List<String> presentSymptoms) {
        String condition = rule.getCondition().toLowerCase();
        String[] conditions = condition.split("&&");

        for (String cond : conditions) {
            String symptom = cond.trim();
            if (!presentSymptoms.contains(symptom)) {
                return false;
            }
        }
        return true;
    }

    private List<Disease> findPossibleDiseases(List<String> presentSymptoms) {
        List<Disease> possibleDiseases = new ArrayList<>();

        for (Disease disease : diseases) {
            for (String symptom : presentSymptoms) {
                if (disease.getSymptoms().contains(symptom)) {
                    possibleDiseases.add(disease);
                    break;
                }
            }
        }

        return possibleDiseases;
    }

    private DiagnosisResult combineDiagnoses(List<ExpertRule> matchedRules,
                                             List<Disease> possibleDiseases,
                                             List<String> presentSymptoms) {
        // Use rules if available (they have higher confidence)
        if (!matchedRules.isEmpty()) {
            ExpertRule bestRule = matchedRules.get(0); // Already sorted by confidence
            Optional<Disease> disease = findDiseaseByName(bestRule.getConclusion());

            if (disease.isPresent()) {
                Disease d = disease.get();
                return new DiagnosisResult(
                        d.getName(),
                        d.getDescription(),
                        d.getTreatment(),
                        bestRule.getConfidence() * 100,
                        presentSymptoms,
                        "Based on expert rule: " + bestRule.getRuleName()
                );
            }
        }

        // Use symptom pattern matching if no rules matched
        if (!possibleDiseases.isEmpty()) {
            Disease bestMatch = findBestMatch(possibleDiseases, presentSymptoms);
            double confidence = calculateConfidence(bestMatch, presentSymptoms);

            return new DiagnosisResult(
                    bestMatch.getName(),
                    bestMatch.getDescription(),
                    bestMatch.getTreatment(),
                    confidence,
                    presentSymptoms,
                    "Based on symptom pattern matching"
            );
        }

        return createUnknownResult(presentSymptoms);
    }

    private Optional<Disease> findDiseaseByName(String name) {
        return diseases.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst();
    }

    private Disease findBestMatch(List<Disease> diseases, List<String> presentSymptoms) {
        Disease bestMatch = diseases.get(0);
        double bestScore = calculateMatchScore(bestMatch, presentSymptoms);

        for (int i = 1; i < diseases.size(); i++) {
            double score = calculateMatchScore(diseases.get(i), presentSymptoms);
            if (score > bestScore) {
                bestMatch = diseases.get(i);
                bestScore = score;
            }
        }

        return bestMatch;
    }

    private double calculateMatchScore(Disease disease, List<String> presentSymptoms) {
        int matchedSymptoms = 0;
        for (String symptom : presentSymptoms) {
            if (disease.getSymptoms().contains(symptom)) {
                matchedSymptoms++;
            }
        }

        if (disease.getSymptoms().isEmpty()) return 0.0;

        return (double) matchedSymptoms / disease.getSymptoms().size();
    }

    private double calculateConfidence(Disease disease, List<String> presentSymptoms) {
        double matchScore = calculateMatchScore(disease, presentSymptoms);
        return Math.min(matchScore * 100, 95.0);
    }

    private DiagnosisResult createNoSymptomsResult() {
        return new DiagnosisResult(
                "No specific disease",
                "Insufficient symptoms for diagnosis",
                "Please provide more symptoms",
                0.0,
                new ArrayList<>(),
                "Consult a healthcare professional for accurate diagnosis"
        );
    }

    private DiagnosisResult createUnknownResult(List<String> presentSymptoms) {
        return new DiagnosisResult(
                "Unknown Condition",
                "No specific disease pattern matched your symptoms",
                "Please consult a healthcare professional",
                0.0,
                presentSymptoms,
                "Seek medical advice for accurate diagnosis"
        );
    }

    public List<QuestionDto> getQuestions() {
        List<QuestionDto> questions = new ArrayList<>();

        for (int i = 0; i < symptoms.size(); i++) {
            Symptom symptom = symptoms.get(i);
            questions.add(new QuestionDto(
                    symptom.getName(),
                    symptom.getQuestion(),
                    i + 1,
                    symptoms.size()
            ));
        }

        return questions;
    }

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("symptomsCount", symptoms.size());
        info.put("diseasesCount", diseases.size());
        info.put("rulesCount", expertRules.size());
        info.put("knowledgeBase", "In-Memory (No Database)");
        info.put("status", "Ready");
        return info;
    }
}