package com.ecommerce.product_service.exception;

import com.ecommerce.product_service.dtos.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SellerNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFound(SellerNotFoundException ex) {
        return new ResponseEntity<>(new MessageResponse(false, HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>(new MessageResponse(false, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
