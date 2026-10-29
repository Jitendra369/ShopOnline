package com.shoponline.product_inventory.controller;

import com.shoponline.product_inventory.dto.InventoryRequestDto;
import com.shoponline.product_inventory.dto.InventoryResponseDto;
import com.shoponline.product_inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponseDto> create(@Valid @RequestBody InventoryRequestDto request) {
        InventoryResponseDto savedInventory = inventoryService.create(request);
        return new ResponseEntity<>(savedInventory, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDto> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    @PutMapping("/{productId}/add")
    public ResponseEntity<Void> addStock(@PathVariable Long productId, @RequestParam @Positive int qty) {
        inventoryService.addStock(productId, qty);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/reduce")
    public ResponseEntity<Void> reduceStock(@PathVariable Long productId, @RequestParam @Positive int qty) {
        inventoryService.reduceStock(productId, qty);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/in-stock")
    public ResponseEntity<Boolean> isInStock(@PathVariable Long productId, @RequestParam @Positive int qty) {
        return ResponseEntity.ok(inventoryService.isInStock(productId, qty));
    }
}
