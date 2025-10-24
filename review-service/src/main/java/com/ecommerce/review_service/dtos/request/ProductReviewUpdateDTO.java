package com.ecommerce.review_service.dtos.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewUpdateDTO {
    private Integer rating;
    private String comment;
}
