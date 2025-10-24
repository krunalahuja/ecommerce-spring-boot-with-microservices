package com.ecommerce.order_service.dtos.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistRequestDTO {
    private Long productId;
}
