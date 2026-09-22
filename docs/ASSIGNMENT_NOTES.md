# Assignment Notes

## Part 1
- POST `/api/v1/risk-score/initiate`
- Bearer scope `write:risk_test`
- Aadhaar masked before persistence
- Transaction saved as PENDING
- Kafka event published
- HTTP 202 returned

## Part 2
- Kafka consumer listens to `Risk_Evaluation_Requested`
- Random score from 1-100
- Atomic state transition provides idempotency
- Webhook outbox row is written in the same database transaction
- Separate scheduled worker sends callback
- 503/network errors use exponential backoff
- Kafka consumer thread is never used for retry waiting

## Part 3
- `read:risk_test` required
- Partner identity is extracted from authenticated token
- Audit query filters by both partner ID and transaction ID
- Composite index supports the query
- Only required fields are selected conceptually; JPA entity mapping keeps the lookup simple
