package com.uidai.sandbox.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mock/partner")
public class MockPartnerController {

    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> callback(
            @RequestBody Map<String, Object> payload) {

        System.out.println("Mock partner received webhook: " + payload);
        return ResponseEntity.ok(Map.of("received", true));
    }
}
