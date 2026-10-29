package com.shoponline.product_inventory.service;

import com.shoponline.product_inventory.dto.InventoryEvent;
import com.shoponline.product_inventory.dto.InventoryEventType;
import com.shoponline.product_inventory.dto.InventoryRequestDto;
import com.shoponline.product_inventory.dto.InventoryResponseDto;
import com.shoponline.product_inventory.entity.Inventory;
import com.shoponline.product_inventory.event.ProductCreatedEvent;
import com.shoponline.product_inventory.exception.ProductNotFoundException;
import com.shoponline.product_inventory.repo.InventoryRepository;
import com.shoponline.product_inventory.webservice.ProductClient;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repo;
    private final KafkaService kafkaService;
    private final ProductClient productClient;

    public InventoryResponseDto create(InventoryRequestDto req) {
        if (repo.findByProductId(req.getProductId()).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock already exists for product " + req.getProductId());

        Inventory inventory = new Inventory();
        inventory.setProductId(req.getProductId());
        inventory.setQuantity(req.getQuantity());
        try {
            InventoryResponseDto saved = mapToDto(repo.save(inventory));
            publish(saved.getProductId(), saved.getQuantity(), InventoryEventType.STOCK_CREATED);
            return saved;
        } catch (DataIntegrityViolationException e) {
            // another request created the same product's stock between the check and the insert
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock already exists for product " + req.getProductId());
        }
    }

    public void createFromProductEvent(ProductCreatedEvent event) {
        // Kafka delivers at least once, so a redelivered event must not create a second stock row
        if (repo.findByProductId(event.getProductId()).isPresent()) {
            log.info("Stock already exists for product {}, skipping duplicate event {}", event.getProductId(), event.getEventId());
            return;
        }

        checkIfProductIsPresent(event.getProductId());
        Inventory inventory = new Inventory();
        inventory.setProductId(event.getProductId());
        inventory.setQuantity(event.getQuantity());
        inventory.setIdemKey(event.getEventId());
        try {
            repo.save(inventory);
//            publish(event.getProductId(), event.getQuantity(), InventoryEventType.STOCK_CREATED);
        } catch (DataIntegrityViolationException e) {
            // a concurrent delivery of the same event inserted the row between the check and the insert
            log.info("Stock already exists for product {}, skipping duplicate event {}", event.getProductId(), event.getEventId());
        }
    }

    private void checkIfProductIsPresent(Long productId) {
        try {
            productClient.getProductById(productId);
        } catch (FeignException.NotFound e) {
            // not retryable, the error handler sends the event straight to the DLT
            throw new ProductNotFoundException("Product not present having productId " + productId);
        }
    }

    public InventoryResponseDto getByProductId(Long productId) {
        return repo.findByProductId(productId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
    }

    @Transactional
    public void addStock(Long productId, int qty) {
        if (repo.addStock(productId, qty) == 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        publish(productId, qty, InventoryEventType.STOCK_ADDED);
    }

    @Transactional
    public void reduceStock(Long productId, int qty) {
        if (repo.reduceStock(productId, qty) == 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        publish(productId, qty, InventoryEventType.STOCK_REDUCED);
    }

    public boolean isInStock(Long productId, int qty) {
        return repo.findByProductId(productId)
                .map(i -> i.getQuantity() >= qty)
                .orElse(false);
    }

    private void publish(Long productId, int quantity, InventoryEventType eventType) {
        kafkaService.sendEvent(new InventoryEvent(productId, quantity, eventType, Instant.now()));
    }

    private InventoryResponseDto mapToDto(Inventory inventory) {
        return new InventoryResponseDto(inventory.getProductId(), inventory.getQuantity());
    }
}
