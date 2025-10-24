package com.ecommerce.review_service.service;

import com.ecommerce.review_service.dtos.request.ProductReviewRequestDTO;
import com.ecommerce.review_service.dtos.request.ProductReviewUpdateDTO;
import com.ecommerce.review_service.dtos.response.DataResponse;
import com.ecommerce.review_service.dtos.response.MessageResponse;
import com.ecommerce.review_service.dtos.response.SingleDataResponse;

public interface ProductReviewService {

    // Add a review by a logged-in user
    MessageResponse addReview(ProductReviewRequestDTO dto, Long loggedInUserId);

    // Update a review owned by the logged-in user
    MessageResponse updateReview(Long reviewId, ProductReviewUpdateDTO dto, Long loggedInUserId);

    // Delete a review owned by the logged-in user
    MessageResponse deleteReview(Long reviewId, Long loggedInUserId);

    // Get all reviews for a specific product
    DataResponse getReviewsByProduct(Long productId);

    // Get all reviews by a specific user
    DataResponse getReviewsByUser(Long userId);

    // Get a single review by its ID
    SingleDataResponse getReviewById(Long reviewId);
}
