package com.uidai.sandbox.service;

import com.uidai.sandbox.model.WebhookOutbox;
import com.uidai.sandbox.repository.WebhookOutboxRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Service
public class WebhookService {

    private final WebhookOutboxRepository repository;
    private final WebClient webClient;

    public WebhookService(WebhookOutboxRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
    }

    public void processDueWebhooks() {
        List<WebhookOutbox> pending =
                repository.findTop20ByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                        "PENDING", Instant.now());

        for (WebhookOutbox event : pending) {
            send(event);
        }
    }

    private void send(WebhookOutbox event) {
        try {
            WebhookPayload payload =
                    new WebhookPayload(
                            event.getTransactionId().toString(),
                            event.getRiskScore(),
                            "COMPLETED");

            webClient.post()
                    .uri(event.getCallbackUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            event.setStatus("SENT");
            event.setLastError(null);
            repository.save(event);

        } catch (Exception ex) {
            int retry = event.getRetryCount() + 1;
            event.setRetryCount(retry);

            // Exponential backoff, capped at 5 minutes.
            long delaySeconds = Math.min(300L, 5L * (1L << Math.min(retry, 6)));
            event.setNextAttemptAt(Instant.now().plusSeconds(delaySeconds));
            event.setLastError(ex.getMessage());
            repository.save(event);
        }
    }

    public record WebhookPayload(
            String transaction_id,
            Integer risk_score,
            String status) {}
}
