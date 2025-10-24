package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
    Optional<Wishlist> findByUserId(Long loggedInUserId);
}
