package com.notification.eventdriven.events.retry;

public final class RetryPolicy {

    private RetryPolicy() {}

    public static final int MAX_RETRIES = 3;

    public static String nextRetryTopic(int retryCount) {
        return switch (retryCount) {
            case 0 -> "notification.retry.5s";
            case 1 -> "notification.retry.30s";
            case 2 -> "notification.retry.5m";
            default -> null; // escalate to DLQ
        };
    }
}
