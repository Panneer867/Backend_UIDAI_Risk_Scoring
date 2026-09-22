# UIDAI Sandbox - Fraud Risk Scoring Pipeline

Technical assignment implementation for the Backend Developer role.

## Stack

- Java 17
- Spring Boot 3.5
- Spring Security
- Spring Kafka
- Spring Data JPA
- H2
- WebClient
- HTML5/CSS/JavaScript
- Docker Compose

## Architecture

```text
Browser UI
    |
    v
POST /api/v1/risk-score/initiate
    |
    +-- Bearer scope: write:risk_test
    |
    v
Save PENDING + masked Aadhaar
    |
    v
Kafka: Risk_Evaluation_Requested
    |
    v
Kafka Consumer
    |
    +-- Atomic idempotent COMPLETED update
    |
    +-- Create webhook outbox row
    |
    v
Webhook Scheduler
    |
    +-- POST partner callback
    +-- exponential backoff on failure
```

## Run locally

### 1. Start Kafka

```bash
docker compose up -d
```

### 2. Start the application

```bash
mvn spring-boot:run
```

Open:

http://localhost:8080/

### 3. Build

```bash
mvn clean test
mvn clean package
```

## Demo tokens

The assignment uses a small demo bearer-token middleware so the reviewer can run it without an external OAuth server.

- `demo-write-token` -> partner_A -> `write:risk_test`, `read:risk_test`
- `demo-read-token` -> partner_A -> `read:risk_test`
- `partner-b-token` -> partner_B -> `read:risk_test`

Production deployment should replace this demo filter with Spring Security OAuth2 Resource Server JWT validation.

## APIs

### Initiate

```bash
curl -i -X POST http://localhost:8080/api/v1/risk-score/initiate \
  -H "Authorization: Bearer demo-write-token" \
  -H "Content-Type: application/json" \
  -d '{
    "partner_id": "partner_A",
    "aadhaar_number": "1234-5678-1234",
    "device_data": {
      "device_id": "DEVICE-001",
      "os": "Android"
    },
    "callback_url": "http://localhost:8080/mock/partner/callback"
  }'
```

Expected: HTTP 202 with a transaction ID.

### Audit

```bash
curl -i \
  -H "Authorization: Bearer demo-read-token" \
  http://localhost:8080/api/v1/risk-score/audit/<TRANSACTION_ID>
```

IDOR test:

```bash
curl -i \
  -H "Authorization: Bearer partner-b-token" \
  http://localhost:8080/api/v1/risk-score/audit/<PARTNER_A_TRANSACTION_ID>
```

Expected: HTTP 404.

## Security decisions

1. `write:risk_test` is required for POST initiation.
2. `read:risk_test` is required for audit.
3. The authenticated partner ID is taken from the security context.
4. Audit query requires both `partner_id` and `transaction_id`.
5. Raw Aadhaar is never persisted.
6. Raw Aadhaar is not logged.
7. Callback retries happen outside Kafka consumer threads.

## Idempotency

The consumer uses:

```sql
UPDATE risk_transactions
SET status = 'COMPLETED', risk_score = ?, completed_at = ?
WHERE transaction_id = ?
  AND status <> 'COMPLETED';
```

Only one duplicate delivery can change the row. The first delivery gets one affected row; later deliveries get zero.

The same DB transaction creates a webhook outbox row, preventing the "transaction completed but webhook task lost" dual-write failure.

## Database optimization

Audit lookup:

```sql
SELECT transaction_id, partner_id, status, risk_score,
       aadhaar_number, created_at, completed_at
FROM risk_transactions
WHERE partner_id = ?
  AND transaction_id = ?;
```

Index:

```sql
CREATE INDEX idx_risk_partner_transaction
ON risk_transactions(partner_id, transaction_id);
```

The query is a point lookup with no joins or table scan. Actual sub-10ms latency must be verified with an execution plan and production-like load test; an index alone cannot mathematically guarantee latency.

## Notes

H2 is used to keep the assignment self-contained. The design maps directly to PostgreSQL/MySQL. Kafka is provided through Docker Compose.

The callback uses a Webhook Outbox + scheduled worker so HTTP retries never block the Kafka consumer queue.
