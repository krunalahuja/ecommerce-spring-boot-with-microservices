package com.ecommerce.order_service.feign;

import com.ecommerce.order_service.dtos.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "product-service", url ="${product.service.url}")
public interface ProductClient {

    @GetMapping("/{productId}")
    ProductResponse getProductById(@PathVariable("productId") Long productId);

    @GetMapping("/batch")
    List<ProductResponse> getProductsByIds(@PathVariable("ids") List<Long> ids);
}
