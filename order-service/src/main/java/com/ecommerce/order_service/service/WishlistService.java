package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dtos.request.WishlistRequestDTO;
import com.ecommerce.order_service.dtos.response.DataResponse;
import com.ecommerce.order_service.dtos.response.MessageResponse;

import java.util.Set;

public interface WishlistService {

    // Add a product to the wishlist
    MessageResponse addProductToWishlist(WishlistRequestDTO dto, Long loggedInUserId);

    // Remove a product from the wishlist
    MessageResponse removeProductFromWishlist(Long productId, Long loggedInUserId);

    // Get all products in the wishlist for the logged-in user
    DataResponse getWishlistByUser(Long loggedInUserId);

    // Clear all products in the wishlist
    MessageResponse clearWishlist(Long loggedInUserId);

    // Place order from wishlist, either selected products or all if productIds is null/empty
    MessageResponse orderFromWishlist(Set<Long> productIds, Long loggedInUserId);
}
