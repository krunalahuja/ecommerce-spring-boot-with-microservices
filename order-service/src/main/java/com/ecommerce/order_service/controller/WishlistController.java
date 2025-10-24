package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dtos.request.WishlistRequestDTO;
import com.ecommerce.order_service.dtos.response.DataResponse;
import com.ecommerce.order_service.dtos.response.MessageResponse;
import com.ecommerce.order_service.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    // Add a product to wishlist
    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addProductToWishlist(@RequestBody WishlistRequestDTO dto,
                                                                @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(wishlistService.addProductToWishlist(dto, loggedInUserId), HttpStatus.OK);
    }

    // Remove a product from wishlist
    @DeleteMapping("/remove")
    public ResponseEntity<MessageResponse> removeProductFromWishlist(@RequestParam Long productId,
                                                                     @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(wishlistService.removeProductFromWishlist(productId, loggedInUserId), HttpStatus.OK);
    }

    // Get all products in wishlist
    @GetMapping
    public ResponseEntity<DataResponse> getWishlistByUser(@RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(wishlistService.getWishlistByUser(loggedInUserId), HttpStatus.OK);
    }

    // Clear wishlist
    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearWishlist(@RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(wishlistService.clearWishlist(loggedInUserId), HttpStatus.OK);
    }

    // Order from wishlist (select all or specific products)
    @PostMapping("/order")
    public ResponseEntity<MessageResponse> orderFromWishlist(@RequestParam(required = false) Set<Long> productIds,
                                                             @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(wishlistService.orderFromWishlist(productIds, loggedInUserId), HttpStatus.OK);
    }
}
