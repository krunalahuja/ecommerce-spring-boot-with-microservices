package com.ecommerce.user_service.controller;

import com.ecommerce.user_service.dtos.request.*;
import com.ecommerce.user_service.dtos.response.DataResponse;
import com.ecommerce.user_service.dtos.response.MessageResponse;
import com.ecommerce.user_service.dtos.response.SingleDataResponse;
import com.ecommerce.user_service.service.PasswordResetService;
import com.ecommerce.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    // ===================== USER =========================

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SingleDataResponse> getUserById(@PathVariable Long userId,
                                                          @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.getUserById(userId, loggedInUsername));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<MessageResponse> updateUser(@PathVariable Long userId,
                                                      @Valid @RequestBody UserUpdateDTO dto,
                                                      @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.updateUser(userId, dto, loggedInUsername));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<MessageResponse> softDeleteUser(@PathVariable Long userId,
                                                          @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.softDeleteUser(userId, loggedInUsername));
    }

    @PutMapping("/restore/{userId}")
    public ResponseEntity<MessageResponse> restoreUser(@PathVariable Long userId,
                                                       @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.restoreUser(userId, loggedInUsername));
    }

    @GetMapping
    public ResponseEntity<DataResponse> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String role,
                                                    @RequestParam(required = false) Boolean includeDeleted) {
        return ResponseEntity.ok(userService.getAllUsers(page, size, role, includeDeleted));
    }

    // ===================== SELLER =========================

    @PostMapping("/seller/register")
    public ResponseEntity<MessageResponse> registerSeller(@Valid @RequestBody SellerRequestDTO dto) {
        return ResponseEntity.ok(userService.registerSeller(dto));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<SingleDataResponse> getSellerById(@PathVariable Long sellerId,
                                                            @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.getSellerById(sellerId, loggedInUsername));
    }

    @GetMapping("/sellers")
    public ResponseEntity<DataResponse> getAllSellers(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(required = false) Boolean includeDeleted) {
        return ResponseEntity.ok(userService.getAllSellers(page, size, includeDeleted));
    }

    @DeleteMapping("/seller/{sellerId}")
    public ResponseEntity<MessageResponse> softDeleteSeller(@PathVariable Long sellerId,
                                                            @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.softDeleteSeller(sellerId, loggedInUsername));
    }

    @PutMapping("/seller/restore/{sellerId}")
    public ResponseEntity<MessageResponse> restoreSeller(@PathVariable Long sellerId,
                                                         @RequestParam String loggedInUsername) {
        return ResponseEntity.ok(userService.restoreSeller(sellerId, loggedInUsername));
    }

    // ===================== PASSWORD RESET =========================

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody PasswordResetRequestDTO dto) {
        return ResponseEntity.ok(passwordResetService.requestPasswordReset(dto));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetDTO dto) {
        return ResponseEntity.ok(passwordResetService.resetPassword(dto));
    }
}
