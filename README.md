**📣 Event-Driven Notification Service**

A backend event-driven notification service built using Spring Boot, Kafka, JPA, and PostgreSQL, designed with idempotency, fault tolerance, and retry safety in mind.

This service consumes domain events from Kafka, stores notifications idempotently, and delivers them using a scheduled retry worker to ensure reliability without blocking Kafka consumers.

_🧠 Why this project exists_

In distributed systems:

Kafka delivers messages at-least-once

Consumers may crash or restart

Duplicate events are inevitable

Side effects (email/SMS) must not break consumption

This project demonstrates a real-world pattern to handle those problems correctly.

**🔑 Core Design Decisions**

_1. Idempotency (Non-negotiable)_

Every event carries a globally unique eventId

eventId has a DB-level UNIQUE constraint

Duplicate Kafka messages are safely ignored

Kafka redelivery is harmless because the database is the source of truth.

_2. Thin Kafka Consumer_

Kafka consumer does no business logic

It delegates immediately to the service layer

No retries, no sending, no DB logic in the listener

Kafka → Consumer → Service → Database

_3. Decoupled Notification Sending_

Notifications are not sent inside Kafka listeners

Sending is handled by a scheduled background job

This avoids blocking Kafka and prevents duplicate side effects

_4. Retry Strategy_

Notifications start in CREATED state

Failed attempts move to FAILED

Retries are bounded by MAX_RETRIES

SENT is a terminal state

Allowed transitions:

CREATED → SENT
CREATED → FAILED
FAILED  → SENT
FAILED  → FAILED (retry++)

**🧩 Event Contract**

NotificationEvent
{
  "eventId": "uuid-string",
  "eventType": "TASK_COMPLETED",
  "message": "Task completed successfully",
  "occurredAt": "2025-01-01T12:00:00Z"
}

eventId → idempotency key

eventType → observability / future routing

message → notification content

occurredAt → audit & debugging

**⚙️ Tech Stack**

Java 17

Spring Boot

Spring Kafka

Spring Data JPA

PostgreSQL

Lombok

Kafka
