package com.expertsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpertSystemBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExpertSystemBackendApplication.class, args);
		System.out.println("✅ Expert System started successfully!");
		System.out.println("✅ No database configured - using in-memory knowledge base");
		System.out.println("✅ Backend running at: http://localhost:8080");
		System.out.println("✅ API endpoints available at: http://localhost:8080/api/expert-system/");
	}
}