package com.ecommerce.review_service.dtos.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewRequestDTO {
    private Long userId;
    private Long productId;
    private Integer rating;
    private String comment;
}
