package com.ecommerce.product_service.feign;

import com.ecommerce.product_service.dtos.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface SellerClient {

    @GetMapping("/seller/{sellerId}")
    UserResponse getSellerById(@PathVariable("sellerId") Long sellerId);
}