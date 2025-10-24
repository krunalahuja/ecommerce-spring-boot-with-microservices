package com.ecommerce.user_service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;
    private String message;
    private String email;
    private String username;
    private Set<String> roles;
}
