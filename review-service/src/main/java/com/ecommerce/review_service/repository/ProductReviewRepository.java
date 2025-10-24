package com.ecommerce.review_service.repository;

import com.ecommerce.product_service.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findAllByProductIdAndIsDeletedFalse(Long productId);
    List<ProductReview> findAllByUserIdAndIsDeletedFalse(Long userId);
    boolean existsByProductIdAndUserId(Long productId, Long userId);;

    <T> ScopedValue<T> findByIdAndIsDeletedFalse(Long reviewId);

    List<ProductReview> findByProductIdAndIsDeletedFalse(Long productId);

    List<ProductReview> findByUserIdAndIsDeletedFalse(Long userId);
}
