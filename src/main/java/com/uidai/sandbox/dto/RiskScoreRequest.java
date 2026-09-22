package com.uidai.sandbox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record RiskScoreRequest(
        @NotBlank @Size(max = 100) String partner_id,
        @NotBlank @Pattern(regexp = "\\d{4}[- ]?\\d{4}[- ]?\\d{4}",
                message = "aadhaar_number must contain 12 digits") String aadhaar_number,
        Map<String, Object> device_data,
        @NotBlank @Size(max = 1000) String callback_url
) {}
