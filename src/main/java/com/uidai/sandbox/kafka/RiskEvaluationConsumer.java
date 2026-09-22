package com.uidai.sandbox.kafka;

import com.uidai.sandbox.dto.RiskEvaluationRequestedEvent;
import com.uidai.sandbox.service.RiskEvaluationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RiskEvaluationConsumer {

    private final RiskEvaluationService service;

    public RiskEvaluationConsumer(RiskEvaluationService service) {
        this.service = service;
    }

    @KafkaListener(
            topics = "Risk_Evaluation_Requested",
            groupId = "risk-evaluation-worker",
            concurrency = "2")
    public void consume(RiskEvaluationRequestedEvent event) {
        service.process(event);
        // No webhook HTTP call here: Kafka consumer threads are not blocked by retries.
    }
}
