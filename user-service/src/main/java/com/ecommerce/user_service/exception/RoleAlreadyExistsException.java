package com.ecommerce.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RoleAlreadyExistsException extends ResponseStatusException {
    public RoleAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
