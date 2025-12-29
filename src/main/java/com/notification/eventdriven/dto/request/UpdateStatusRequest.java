package com.notification.eventdriven.dto.request;

import com.notification.eventdriven.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull
    private NotificationStatus status;

    public UpdateStatusRequest() {
        // required for JSON deserialization
    }

    public UpdateStatusRequest(NotificationStatus status) {
        this.status = status;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
