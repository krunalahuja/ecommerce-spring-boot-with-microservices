package com.ecommerce.review_service.controller;

import com.ecommerce.review_service.dtos.request.ProductReviewRequestDTO;
import com.ecommerce.review_service.dtos.request.ProductReviewUpdateDTO;
import com.ecommerce.review_service.dtos.response.DataResponse;
import com.ecommerce.review_service.dtos.response.MessageResponse;
import com.ecommerce.review_service.dtos.response.SingleDataResponse;
import com.ecommerce.review_service.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    @PostMapping
    public ResponseEntity<MessageResponse> addReview(
            @RequestBody ProductReviewRequestDTO dto,
            @RequestParam Long loggedInUserId
    ) {
        MessageResponse response = reviewService.addReview(dto, loggedInUserId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ProductReviewUpdateDTO dto,
            @RequestParam Long loggedInUserId
    ) {
        MessageResponse response = reviewService.updateReview(reviewId, dto, loggedInUserId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MessageResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long loggedInUserId
    ) {
        MessageResponse response = reviewService.deleteReview(reviewId, loggedInUserId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<DataResponse> getReviewsByProduct(@PathVariable Long productId) {
        DataResponse response = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DataResponse> getReviewsByUser(@PathVariable Long userId) {
        DataResponse response = reviewService.getReviewsByUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<SingleDataResponse> getReviewById(@PathVariable Long reviewId) {
        SingleDataResponse response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(response);
    }
}
