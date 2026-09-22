package com.uidai.sandbox.service;

import org.springframework.stereotype.Service;

@Service
public class AadhaarMaskingService {

    public String mask(String aadhaar) {
        String digits = aadhaar.replaceAll("\\D", "");

        if (digits.length() != 12) {
            throw new IllegalArgumentException("Aadhaar number must contain exactly 12 digits");
        }

        // Raw Aadhaar is never persisted.
        return "XXXX-XXXX-" + digits.substring(8);
    }
}
