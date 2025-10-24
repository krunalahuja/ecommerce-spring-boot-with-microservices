package com.ecommerce.review_service.service.Impl;

import com.ecommerce.product_service.entity.ProductReview;
import com.ecommerce.review_service.dtos.request.ProductReviewRequestDTO;
import com.ecommerce.review_service.dtos.request.ProductReviewUpdateDTO;
import com.ecommerce.review_service.dtos.response.*;
import com.ecommerce.review_service.feign.ProductClient;
import com.ecommerce.review_service.feign.UserClient;
import com.ecommerce.review_service.repository.ProductReviewRepository;
import com.ecommerce.review_service.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Override
    public MessageResponse addReview(ProductReviewRequestDTO dto, Long loggedInUserId) {
        UserResponse user = userClient.getUserById(loggedInUserId);
        if (user == null) {
            return new MessageResponse(false, HttpStatus.NOT_FOUND, "User not found");
        }

        ProductResponse product = productClient.getProductById(dto.getProductId());
        if (product == null || Boolean.TRUE.equals(product.getIsDeleted())) {
            return new MessageResponse(false, HttpStatus.NOT_FOUND, "Product not found or deleted");
        }

        ProductReview review = new ProductReview();
        review.setUserId(user.getId());
        review.setProductId(product.getId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setIsDeleted(false);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        reviewRepository.save(review);
        return new MessageResponse(true, HttpStatus.CREATED, "Review added successfully");
    }

    @Override
    public MessageResponse updateReview(Long reviewId, ProductReviewUpdateDTO dto, Long loggedInUserId) {
        ProductReview review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null || Boolean.TRUE.equals(review.getIsDeleted())) {
            return new MessageResponse(false, HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!review.getUserId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Not authorized to update this review");
        }

        if (dto.getRating() != null) review.setRating(dto.getRating());
        if (dto.getComment() != null) review.setComment(dto.getComment());

        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        return new MessageResponse(true, HttpStatus.OK, "Review updated successfully");
    }

    @Override
    public MessageResponse deleteReview(Long reviewId, Long loggedInUserId) {
        ProductReview review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null || Boolean.TRUE.equals(review.getIsDeleted())) {
            return new MessageResponse(false, HttpStatus.NOT_FOUND, "Review not found");
        }

        if (!review.getUserId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Not authorized to delete this review");
        }

        review.setIsDeleted(true);
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        return new MessageResponse(true, HttpStatus.OK, "Review deleted successfully");
    }

    @Override
    public DataResponse getReviewsByProduct(Long productId) {
        ProductResponse product = productClient.getProductById(productId);
        if (product == null || Boolean.TRUE.equals(product.getIsDeleted())) {
            return new DataResponse(false, HttpStatus.NOT_FOUND, Collections.emptyList(), 0, 0, 0, 0);
        }

        List<ProductReview> reviews = reviewRepository.findByProductIdAndIsDeletedFalse(productId);
        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(reviews), reviews.size(), 1, 0, reviews.size());
    }

    @Override
    public DataResponse getReviewsByUser(Long userId) {
        UserResponse user = userClient.getUserById(userId);
        if (user == null) {
            return new DataResponse(false, HttpStatus.NOT_FOUND, Collections.emptyList(), 0, 0, 0, 0);
        }

        List<ProductReview> reviews = reviewRepository.findByUserIdAndIsDeletedFalse(userId);
        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(reviews), reviews.size(), 1, 0, reviews.size());
    }

    @Override
    public SingleDataResponse getReviewById(Long reviewId) {
        ProductReview review = reviewRepository.findById(reviewId).orElse(null);
        if (review == null || Boolean.TRUE.equals(review.getIsDeleted())) {
            return new SingleDataResponse(false, HttpStatus.NOT_FOUND, "Review not found");
        }

        return new SingleDataResponse(true, HttpStatus.OK, review);
    }
}
