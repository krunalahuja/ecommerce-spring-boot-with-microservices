package com.ecommerce.auth_service.controller;

import com.ecommerce.auth_service.dtos.AuthRequest;
import com.ecommerce.auth_service.dtos.AuthResponse;
import com.ecommerce.auth_service.feign.UserServiceFeignClient;
import com.ecommerce.auth_service.service.JwtService;
import com.ecommerce.auth_service.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserServiceFeignClient userServiceFeignClient;

    // LOGIN
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        try {
            // Call user-service to validate credentials
            boolean isValid = userServiceFeignClient.validateCredentials(request.getEmail(), request.getPassword());
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email/password");
            }

            // Fetch roles from user-service
            List<String> roles = userServiceFeignClient.getRolesByEmail(request.getEmail());

            // Generate JWT
            String token = jwtService.generateToken(request.getEmail(), roles);

            return new AuthResponse(token);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed: " + e.getMessage());
        }
    }

    // LOGOUT
    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.addToken(token);
            return "Logged out successfully";
        }
        return "No token provided";
    }
}
