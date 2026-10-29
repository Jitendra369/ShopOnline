package com.shoponline.product_inventory.service;

import com.shoponline.product_inventory.dto.InventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(InventoryEvent event) {
        // inside a transaction, publish only after commit so rolled-back changes never produce an event
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(event);
                }
            });
        } else {
            send(event);
        }
    }

    @KafkaListener(topics = "${app.kafka.topic.inventory}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveEvent(InventoryEvent event) {
        log.info("Received event {}", event);
    }

    private void send(InventoryEvent event) {
        kafkaTemplate.sendDefault(event.getProductId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event {}", event, ex);
                    } else {
                        log.info("Sent event {} to partition {} offset {}", event,
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
