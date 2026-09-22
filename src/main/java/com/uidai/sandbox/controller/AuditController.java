package com.uidai.sandbox.controller;

import com.uidai.sandbox.dto.AuditResponse;
import com.uidai.sandbox.repository.RiskTransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/risk-score")
public class AuditController {

    private final RiskTransactionRepository repository;

    public AuditController(RiskTransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/audit/{transactionId}")
    @PreAuthorize("hasAuthority('SCOPE_read:risk_test')")
    public ResponseEntity<AuditResponse> getAudit(
            @PathVariable UUID transactionId,
            Authentication authentication) {

        /*
         * IDOR FIX:
         * authentication.getName() is the partner identity extracted from
         * the validated Bearer token. It is NOT taken from the path/query.
         *
         * Querying by BOTH partner_id and transaction_id ensures:
         * partner_A + partner_B transaction ID => no record returned.
         */
        String authenticatedPartnerId = authentication.getName();

        return repository
                .findByPartnerIdAndTransactionId(
                        authenticatedPartnerId,
                        transactionId)
                .map(tx -> ResponseEntity.ok(AuditResponse.from(tx)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
