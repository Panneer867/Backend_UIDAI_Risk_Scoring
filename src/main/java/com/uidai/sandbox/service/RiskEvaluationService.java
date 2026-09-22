package com.uidai.sandbox.service;

import com.uidai.sandbox.dto.RiskEvaluationRequestedEvent;
import com.uidai.sandbox.model.RiskStatus;
import com.uidai.sandbox.repository.RiskTransactionRepository;
import com.uidai.sandbox.model.WebhookOutbox;
import com.uidai.sandbox.repository.WebhookOutboxRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RiskEvaluationService {

    private final RiskTransactionRepository transactionRepository;
    private final WebhookOutboxRepository outboxRepository;

    public RiskEvaluationService(
            RiskTransactionRepository transactionRepository,
            WebhookOutboxRepository outboxRepository) {
        this.transactionRepository = transactionRepository;
        this.outboxRepository = outboxRepository;
    }

    /**
     * Idempotent business operation.
     *
     * The conditional UPDATE guarantees only one Kafka delivery can
     * transition a transaction to COMPLETED. The webhook outbox row is
     * created in the same database transaction, avoiding the classic
     * "DB updated but webhook task lost" dual-write problem.
     */
    @Transactional
    public void process(RiskEvaluationRequestedEvent event) {

        int score = ThreadLocalRandom.current().nextInt(1, 101);

        int updatedRows = transactionRepository.markCompletedIfNotCompleted(
                event.transactionId(),
                score,
                RiskStatus.COMPLETED,
                Instant.now());

        if (updatedRows == 0) {
            // Duplicate Kafka event. Do not generate/send another business result.
            return;
        }

        WebhookOutbox outbox = new WebhookOutbox();
        outbox.setTransactionId(event.transactionId());
        outbox.setCallbackUrl(event.callbackUrl());
        outbox.setRiskScore(score);
        outbox.setStatus("PENDING");
        outbox.setRetryCount(0);
        outbox.setNextAttemptAt(Instant.now());

        outboxRepository.save(outbox);
    }
}
