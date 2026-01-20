package com.notification.eventdriven.exceptions;

public class PermanentNotificationException extends RuntimeException {
    public PermanentNotificationException(String message) {
        super(message);
    }

    public PermanentNotificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
