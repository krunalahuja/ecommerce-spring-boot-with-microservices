package com.ecommerce.user_service.repository;

import com.ecommerce.user_service.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
