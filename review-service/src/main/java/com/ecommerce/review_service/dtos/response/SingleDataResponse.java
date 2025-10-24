package com.ecommerce.review_service.dtos.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleDataResponse {
    private Boolean success;
    private HttpStatus status;
    private Object data;
}
