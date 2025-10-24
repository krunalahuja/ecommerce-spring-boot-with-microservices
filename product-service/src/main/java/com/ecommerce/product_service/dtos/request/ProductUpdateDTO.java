package com.ecommerce.product_service.dtos.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String company;
}