package com.notification.eventdriven.events.producer.impl;

import com.notification.eventdriven.events.NotificationEvent;
import com.notification.eventdriven.events.RetryHeaders;
import com.notification.eventdriven.events.producer.RetryProducer;
import com.notification.eventdriven.events.retry.RetryPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetryProducerImpl implements RetryProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public RetryProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(
            NotificationEvent event,
            int currentRetry,
            Exception cause
    ) {

        String nextTopic = RetryPolicy.nextRetryTopic(currentRetry);

        if (nextTopic == null) {
            throw new IllegalStateException("Retry exhausted, should go to DLQ");
        }

        log.warn(
                "Retrying event {} → topic {} (attempt {})",
                event.getEventId(),
                nextTopic,
                currentRetry + 1
        );

        kafkaTemplate.send(
                MessageBuilder
                        .withPayload(event)
                        .setHeader(KafkaHeaders.TOPIC, nextTopic)
                        .setHeader(RetryHeaders.RETRY_COUNT, currentRetry + 1)
                        .setHeader(RetryHeaders.ORIGINAL_TOPIC, "notification.main")
                        .setHeader(RetryHeaders.ERROR_REASON, cause.getMessage())
                        .build()
        );
    }
}
