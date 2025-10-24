package com.ecommerce.review_service.dtos.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Boolean success;
    private HttpStatus status;
    private String message;
}
