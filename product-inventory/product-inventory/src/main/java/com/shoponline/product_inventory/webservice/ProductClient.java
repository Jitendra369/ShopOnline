package com.shoponline.product_inventory.webservice;

import com.shoponline.product_inventory.webservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "http://localhost:8083/api/core/product"
)
public interface ProductClient {

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable("id") Long id);
}
