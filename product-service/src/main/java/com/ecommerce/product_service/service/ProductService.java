package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dtos.request.ProductRequestDTO;
import com.ecommerce.product_service.dtos.request.ProductUpdateDTO;
import com.ecommerce.product_service.dtos.response.DataResponse;
import com.ecommerce.product_service.dtos.response.MessageResponse;
import com.ecommerce.product_service.dtos.response.SingleDataResponse;

public interface ProductService {

    // Seller creates a product
    MessageResponse createProduct(ProductRequestDTO dto, Long loggedInUserId);

    // Seller updates their own product
    MessageResponse updateProduct(Long productId, ProductUpdateDTO dto, Long loggedInUserId);

    // Seller soft-deletes their product
    MessageResponse softDeleteProduct(Long productId, Long loggedInUserId);

    // Seller restores their soft-deleted product
    MessageResponse restoreProduct(Long productId, Long loggedInUserId);

    // Get single product (public)
    SingleDataResponse getProductById(Long productId);

    // Get all products (public + optional filters)
    DataResponse getAllProducts(int page, int size, String category, Boolean includeDeleted);

    // Get all products by specific seller
    DataResponse getProductsBySeller(Long sellerId, int page, int size);
}
