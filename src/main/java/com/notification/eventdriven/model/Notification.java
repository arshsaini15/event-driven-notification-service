package com.notification.eventdriven.model;

import com.notification.eventdriven.enums.FailureType;
import com.notification.eventdriven.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = "eventId")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // idempotency key from Kafka event
    @Column(nullable = false, unique = true, updatable = false)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    private FailureType failureType;

    @Column(nullable = false)
    private int retryCount;

    // for delayed retries
    private Instant nextRetryAt;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Notification(String eventId, String message) {
        this.eventId = eventId;
        this.message = message;
        this.status = NotificationStatus.CREATED;
        this.retryCount = 0;
    }

    /* ---------- Domain Rules ---------- */

    public void markProcessing() {
        assertStatus(NotificationStatus.CREATED, NotificationStatus.RETRYING);
        this.status = NotificationStatus.PROCESSING;
    }

    public void markSent() {
        assertStatus(NotificationStatus.PROCESSING);
        this.failureType = null;
        this.nextRetryAt = null;
        this.status = NotificationStatus.SENT;
    }

    public void markRetry(FailureType failureType, Instant nextRetryAt) {
        assertStatus(NotificationStatus.PROCESSING);
        this.failureType = failureType;
        this.retryCount++;
        this.nextRetryAt = nextRetryAt;
        this.status = NotificationStatus.RETRYING;
    }

    public void markDead(FailureType failureType) {
        assertStatus(NotificationStatus.PROCESSING, NotificationStatus.RETRYING);
        this.failureType = failureType;
        this.status = NotificationStatus.DEAD;
    }

    private void assertStatus(NotificationStatus... allowed) {
        for (NotificationStatus s : allowed) {
            if (this.status == s) return;
        }
        throw new IllegalStateException(
                "Illegal state transition from " + this.status
        );
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
