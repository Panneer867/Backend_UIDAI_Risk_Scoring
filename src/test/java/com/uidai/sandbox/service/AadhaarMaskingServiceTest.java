package com.uidai.sandbox.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AadhaarMaskingServiceTest {

    private final AadhaarMaskingService service = new AadhaarMaskingService();

    @Test
    void shouldMaskAadhaar() {
        assertEquals("XXXX-XXXX-1234",
                service.mask("1234-5678-1234"));
    }

    @Test
    void shouldRejectInvalidAadhaar() {
        assertThrows(IllegalArgumentException.class,
                () -> service.mask("123456"));
    }
}
