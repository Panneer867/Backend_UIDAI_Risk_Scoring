package com.uidai.sandbox.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebhookScheduler {

    private final WebhookService webhookService;

    public WebhookScheduler(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Runs independently from Kafka consumer threads.
     * Failed partner calls are retried later and therefore do not block
     * the Kafka queue.
     */
    @Scheduled(fixedDelay = 2000)
    public void retryWebhooks() {
        webhookService.processDueWebhooks();
    }
}
