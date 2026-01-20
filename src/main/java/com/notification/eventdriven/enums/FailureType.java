package com.notification.eventdriven.enums;

public enum FailureType {
    TRANSIENT,      // timeouts, 5xx, rate limits
    PERMANENT       // validation, bad payload, 4xx
}
