package com.ecommerce.product_service.exception;

public class SellerNotFoundException extends RuntimeException {
    public SellerNotFoundException(Long sellerId) {
        super("Seller not found with id: " + sellerId);
    }
}