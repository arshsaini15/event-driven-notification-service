package com.notification.eventdriven.events;

public final class RetryHeaders {

    private RetryHeaders() {}

    public static final String RETRY_COUNT = "x-retry-count";
    public static final String ORIGINAL_TOPIC = "x-original-topic";
    public static final String ERROR_REASON = "x-error-reason";
}
