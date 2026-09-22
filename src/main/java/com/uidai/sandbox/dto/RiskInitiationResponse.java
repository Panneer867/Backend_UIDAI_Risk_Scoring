package com.uidai.sandbox.dto;

import java.util.UUID;

public record RiskInitiationResponse(UUID transaction_id, String status) {}
