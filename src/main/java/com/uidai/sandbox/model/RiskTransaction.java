package com.uidai.sandbox.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "risk_transactions",
    indexes = {
        @Index(name = "idx_risk_partner_transaction", columnList = "partner_id,transaction_id")
    }
)
public class RiskTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, updatable = false)
    private UUID transactionId;

    @Column(name = "partner_id", nullable = false, length = 100)
    private String partnerId;

    @Column(name = "aadhaar_number", nullable = false, length = 19)
    private String maskedAadhaar;

    @Column(name = "device_data", nullable = false, length = 4000)
    private String deviceData;

    @Column(name = "callback_url", nullable = false, length = 1000)
    private String callbackUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskStatus status;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Version
    private Long version;

    public Long getId() { return id; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public String getPartnerId() { return partnerId; }
    public void setPartnerId(String partnerId) { this.partnerId = partnerId; }
    public String getMaskedAadhaar() { return maskedAadhaar; }
    public void setMaskedAadhaar(String maskedAadhaar) { this.maskedAadhaar = maskedAadhaar; }
    public String getDeviceData() { return deviceData; }
    public void setDeviceData(String deviceData) { this.deviceData = deviceData; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    public RiskStatus getStatus() { return status; }
    public void setStatus(RiskStatus status) { this.status = status; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
