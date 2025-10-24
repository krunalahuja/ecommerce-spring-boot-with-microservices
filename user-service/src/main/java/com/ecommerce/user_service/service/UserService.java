package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dtos.request.SellerRequestDTO;
import com.ecommerce.user_service.dtos.request.UserRequestDTO;
import com.ecommerce.user_service.dtos.request.UserUpdateDTO;
import com.ecommerce.user_service.dtos.response.MessageResponse;
import com.ecommerce.user_service.dtos.response.DataResponse;
import com.ecommerce.user_service.dtos.response.SingleDataResponse;

public interface UserService {

    // ==================== USER ====================
    MessageResponse createUser(UserRequestDTO dto);
    MessageResponse updateUser(Long userId, UserUpdateDTO dto, String loggedInUsername);
    MessageResponse softDeleteUser(Long userId, String loggedInUsername);
    MessageResponse restoreUser(Long userId, String loggedInUsername);
    SingleDataResponse getUserById(Long userId, String loggedInUsername);
    DataResponse getAllUsers(int page, int size, String roleFilter, Boolean includeDeleted);

    // ==================== SELLER ====================
    MessageResponse registerSeller(SellerRequestDTO dto);
    SingleDataResponse getSellerById(Long sellerId, String loggedInUsername);
    DataResponse getAllSellers(int page, int size, Boolean includeDeleted);
    MessageResponse softDeleteSeller(Long sellerId, String loggedInUsername);
    MessageResponse restoreSeller(Long sellerId, String loggedInUsername);
}
