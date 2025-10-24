package com.ecommerce.order_service.exception;

public class WishlistNotFoundException extends RuntimeException {
    public WishlistNotFoundException(Long userId) {
        super("Wishlist not found for user id: " + userId);
    }
}
