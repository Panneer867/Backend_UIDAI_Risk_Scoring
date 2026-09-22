package com.uidai.sandbox.dto;

import java.util.UUID;

public record RiskEvaluationRequestedEvent(
        UUID transactionId,
        String partnerId,
        String callbackUrl
) {}
