package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dtos.request.PasswordResetDTO;
import com.ecommerce.user_service.dtos.request.PasswordResetRequestDTO;
import com.ecommerce.user_service.dtos.response.MessageResponse;

public interface PasswordResetService {
    MessageResponse requestPasswordReset(PasswordResetRequestDTO dto);
    MessageResponse resetPassword(PasswordResetDTO dto);
}