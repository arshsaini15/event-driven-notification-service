package com.notification.eventdriven.exceptions;

public class TransientNotificationException extends RuntimeException {
    public TransientNotificationException(String message) {
        super(message);
    }

    public TransientNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
