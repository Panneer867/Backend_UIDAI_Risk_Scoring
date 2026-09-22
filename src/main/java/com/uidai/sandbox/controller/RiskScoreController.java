package com.uidai.sandbox.controller;

import com.uidai.sandbox.dto.RiskInitiationResponse;
import com.uidai.sandbox.dto.RiskScoreRequest;
import com.uidai.sandbox.service.RiskScoreService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/risk-score")
public class RiskScoreController {

    private final RiskScoreService service;

    public RiskScoreController(RiskScoreService service) {
        this.service = service;
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasAuthority('SCOPE_write:risk_test')")
    public ResponseEntity<RiskInitiationResponse> initiate(
            @Valid @RequestBody RiskScoreRequest request) {

        return ResponseEntity.accepted().body(service.initiate(request));
    }
}
