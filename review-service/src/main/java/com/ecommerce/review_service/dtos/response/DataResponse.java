package com.ecommerce.review_service.dtos.response;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataResponse {
    private Boolean success;
    private HttpStatus status;
    private List<Object> data;
    private long totalElements;
    private int totalPages;
    private int page;
    private int size;
}
