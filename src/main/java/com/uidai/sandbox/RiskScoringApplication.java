package com.uidai.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiskScoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskScoringApplication.class, args);
    }
}
