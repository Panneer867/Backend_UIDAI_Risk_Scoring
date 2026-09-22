package com.uidai.sandbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uidai.sandbox.dto.RiskEvaluationRequestedEvent;
import com.uidai.sandbox.dto.RiskInitiationResponse;
import com.uidai.sandbox.dto.RiskScoreRequest;
import com.uidai.sandbox.model.RiskStatus;
import com.uidai.sandbox.model.RiskTransaction;
import com.uidai.sandbox.repository.RiskTransactionRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RiskScoreService {

    public static final String TOPIC = "Risk_Evaluation_Requested";

    private final RiskTransactionRepository repository;
    private final AadhaarMaskingService maskingService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public RiskScoreService(
            RiskTransactionRepository repository,
            AadhaarMaskingService maskingService,
            KafkaTemplate<String, Object> kafkaTemplate,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.maskingService = maskingService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RiskInitiationResponse initiate(RiskScoreRequest request) {

        UUID transactionId = UUID.randomUUID();

        RiskTransaction tx = new RiskTransaction();
        tx.setTransactionId(transactionId);
        tx.setPartnerId(request.partner_id());
        tx.setMaskedAadhaar(maskingService.mask(request.aadhaar_number()));
        tx.setDeviceData(toJson(request.device_data()));
        tx.setCallbackUrl(request.callback_url());
        tx.setStatus(RiskStatus.PENDING);
        tx.setCreatedAt(Instant.now());

        repository.save(tx);

        RiskEvaluationRequestedEvent event =
                new RiskEvaluationRequestedEvent(
                        transactionId,
                        request.partner_id(),
                        request.callback_url());

        kafkaTemplate.send(TOPIC, transactionId.toString(), event);

        return new RiskInitiationResponse(transactionId, RiskStatus.PENDING.name());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? java.util.Map.of() : value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid device_data", e);
        }
    }
}
