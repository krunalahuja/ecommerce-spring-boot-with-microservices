package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.dtos.request.ProductRequestDTO;
import com.ecommerce.product_service.dtos.request.ProductUpdateDTO;
import com.ecommerce.product_service.dtos.response.DataResponse;
import com.ecommerce.product_service.dtos.response.MessageResponse;
import com.ecommerce.product_service.dtos.response.SingleDataResponse;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Create new product (seller must be logged in)
    @PostMapping
    public ResponseEntity<MessageResponse> createProduct(@RequestBody ProductRequestDTO dto) {
        Long loggedInUserId = null; // placeholder if needed later
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(dto, loggedInUserId));
    }

    // Update product by id (seller can only update their own)
    @PutMapping("/{productId}")
    public ResponseEntity<MessageResponse> updateProduct(@PathVariable Long productId,
                                                         @RequestBody ProductUpdateDTO dto) {
        Long loggedInUserId = null;
        return ResponseEntity.ok(productService.updateProduct(productId, dto, loggedInUserId));
    }

    // Soft delete product (seller only)
    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponse> softDeleteProduct(@PathVariable Long productId) {
        Long loggedInUserId = null;
        return ResponseEntity.ok(productService.softDeleteProduct(productId, loggedInUserId));
    }

    // Restore soft deleted product (seller only)
    @PutMapping("/restore/{productId}")
    public ResponseEntity<MessageResponse> restoreProduct(@PathVariable Long productId) {
        Long loggedInUserId = null;
        return ResponseEntity.ok(productService.restoreProduct(productId, loggedInUserId));
    }

    // Get product by id (public)
    @GetMapping("/{productId}")
    public ResponseEntity<SingleDataResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    // Get all products (public, with optional filters)
    @GetMapping
    public ResponseEntity<DataResponse> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(required = false) String category,
                                                       @RequestParam(required = false) Boolean includeDeleted) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, category, includeDeleted));
    }

    // Get products by seller (public or admin)
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<DataResponse> getProductsBySeller(@PathVariable Long sellerId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId, page, size));
    }
}
