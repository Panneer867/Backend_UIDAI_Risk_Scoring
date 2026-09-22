package com.uidai.sandbox.repository;

import com.uidai.sandbox.model.RiskStatus;
import com.uidai.sandbox.model.RiskTransaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RiskTransactionRepository extends JpaRepository<RiskTransaction, Long> {

    /*
     * PART 3 - Optimized IDOR-safe query.
     *
     * Exact SQL shape:
     * SELECT transaction_id, partner_id, status, risk_score,
     *        aadhaar_number, created_at, completed_at
     * FROM risk_transactions
     * WHERE partner_id = ? AND transaction_id = ?;
     *
     * Index:
     * CREATE INDEX idx_risk_partner_transaction
     * ON risk_transactions(partner_id, transaction_id);
     *
     * transaction_id is also UNIQUE, so the lookup is a point lookup.
     * Only the required record is returned; no joins or table scan.
     *
     * IMPORTANT: partner_id comes from the authenticated Bearer token,
     * never from the URL or request body. This closes IDOR.
     */
    Optional<RiskTransaction> findByPartnerIdAndTransactionId(
            String partnerId, UUID transactionId);

    /*
     * Atomic state transition used for idempotency.
     * If the same Kafka event is delivered twice, only the first
     * COMPLETED transition returns updatedRows=1.
     */
    @Modifying
    @Query("""
        update RiskTransaction r
           set r.status = :completed,
               r.riskScore = :score,
               r.completedAt = :completedAt
         where r.transactionId = :transactionId
           and r.status <> :completed
    """)
    int markCompletedIfNotCompleted(
            @Param("transactionId") UUID transactionId,
            @Param("score") int score,
            @Param("completed") RiskStatus completed,
            @Param("completedAt") Instant completedAt);
}
