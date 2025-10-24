package com.ecommerce.user_service.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetRequestDTO {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;
}
