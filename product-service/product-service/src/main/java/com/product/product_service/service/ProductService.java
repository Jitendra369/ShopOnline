package com.product.product_service.service;

import com.product.product_service.Sequence;
import com.product.product_service.dto.ProductDTO;
import com.product.product_service.entity.ProductEntity;
import com.product.product_service.event.ProductCreatedEvent;
import com.product.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final KafkaService kafkaService;

    public ProductDTO saveProductDetails(ProductDTO productDTO) {
        ProductEntity product = mapToEntity(productDTO);
        Long sequence = sequenceGeneratorService.generateSequence(String.valueOf(Sequence.PRODUCT_SEQUENCE));
        product.setId(sequence);
        ProductEntity savedProduct = productRepository.save(product);
        kafkaService.sendProductCreatedEvent(ProductCreatedEvent.of(savedProduct.getId(), savedProduct.getStockInQuantity()));
        return mapToDTO(savedProduct);
    }

    public Page<ProductDTO> getAllProducts(int page, int size) {
        Sort latestFirst = Sort.by(Sort.Direction.DESC, "createdDate")
                .and(Sort.by(Sort.Direction.DESC, "id"));
        Pageable pageable = PageRequest.of(page, size, latestFirst);
        return productRepository.findAll(pageable).map(this::mapToDTO);
    }

    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found having id " + id));
    }


    private ProductEntity mapToEntity(ProductDTO dto) {
        ProductEntity product = new ProductEntity();
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCurrency(dto.getCurrency());
        product.setCategory(dto.getCategory());
        product.setCategoryId(dto.getCategoryId());
        product.setStockInQuantity(dto.getStockInQuantity());
        product.setImageUrl(dto.getImageUrl());
        product.setActive(dto.isActive());
        return product;
    }

    private ProductDTO mapToDTO(ProductEntity product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCurrency(product.getCurrency());
        dto.setCategory(product.getCategory());
        dto.setCategoryId(product.getCategoryId());
        dto.setStockInQuantity(product.getStockInQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setActive(product.isActive());
        return dto;
    }
}
