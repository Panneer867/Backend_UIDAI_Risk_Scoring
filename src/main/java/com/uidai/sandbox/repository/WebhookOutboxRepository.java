package com.uidai.sandbox.repository;

import com.uidai.sandbox.model.WebhookOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookOutboxRepository extends JpaRepository<WebhookOutbox, Long> {

    Optional<WebhookOutbox> findByTransactionId(UUID transactionId);

    List<WebhookOutbox> findTop20ByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
            String status, Instant now);
}
