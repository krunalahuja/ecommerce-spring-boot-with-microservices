package com.ecommerce.user_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetDTO {

    @NotBlank(message = "Token must not be blank")
    private String token;

    @NotBlank(message = "Password must not be blank")
    private String newPassword;
}
