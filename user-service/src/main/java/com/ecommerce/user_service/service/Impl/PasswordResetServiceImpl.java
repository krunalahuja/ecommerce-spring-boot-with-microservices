package com.ecommerce.user_service.service.Impl;

import com.ecommerce.user_service.dtos.request.PasswordResetDTO;
import com.ecommerce.user_service.dtos.request.PasswordResetRequestDTO;
import com.ecommerce.user_service.dtos.response.MessageResponse;
import com.ecommerce.user_service.entity.PasswordResetToken;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.repository.PasswordResetTokenRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int EXPIRATION_MINUTES = 30;

    @Override
    public MessageResponse requestPasswordReset(PasswordResetRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new UserNotFoundException("User not found with this email");
        }

        String tokenStr = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .token(tokenStr)
                .expiryDate(expiry)
                .used(false)
                .build();

        tokenRepository.save(token);

        // TODO: Integrate email service to send token to user's email
        System.out.println("Password reset token: " + tokenStr);

        return new MessageResponse(true, HttpStatus.OK, "Password reset token sent to email");
    }

    @Override
    public MessageResponse resetPassword(PasswordResetDTO dto) {
        PasswordResetToken token = tokenRepository.findByTokenAndUsedFalse(dto.getToken());
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return new MessageResponse(true, HttpStatus.OK, "Password reset successfully");
    }

}
