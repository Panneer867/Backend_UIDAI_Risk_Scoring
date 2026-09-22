package com.uidai.sandbox.dto;

import com.uidai.sandbox.model.RiskTransaction;
import java.time.Instant;
import java.util.UUID;

public record AuditResponse(
        UUID transaction_id,
        String partner_id,
        String status,
        Integer risk_score,
        String masked_aadhaar,
        Instant created_at,
        Instant completed_at
) {
    public static AuditResponse from(RiskTransaction r) {
        return new AuditResponse(
                r.getTransactionId(),
                r.getPartnerId(),
                r.getStatus().name(),
                r.getRiskScore(),
                r.getMaskedAadhaar(),
                r.getCreatedAt(),
                r.getCompletedAt()
        );
    }
}
