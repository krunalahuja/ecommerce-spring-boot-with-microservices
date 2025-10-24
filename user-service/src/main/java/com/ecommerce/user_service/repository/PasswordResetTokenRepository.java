package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.PasswordResetToken;
import com.ecommerce.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByTokenAndUsedFalse(String token);
    PasswordResetToken findByUserAndUsedFalse(User user);
}
