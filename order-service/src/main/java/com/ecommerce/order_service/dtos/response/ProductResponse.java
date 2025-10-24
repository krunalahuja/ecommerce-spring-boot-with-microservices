package com.ecommerce.order_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String company;
    private Double price;
    private Long sellerId;
    private Integer stock;
    private Boolean isDeleted;
}
