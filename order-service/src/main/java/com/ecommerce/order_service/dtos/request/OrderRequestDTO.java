package com.ecommerce.order_service.dtos.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    private Long productId;
    private Integer quantity;
}
