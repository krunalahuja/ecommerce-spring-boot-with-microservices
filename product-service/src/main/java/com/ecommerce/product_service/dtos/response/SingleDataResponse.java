package com.ecommerce.product_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleDataResponse {
    private boolean success;
    private HttpStatus status;
    private Object data;
}
