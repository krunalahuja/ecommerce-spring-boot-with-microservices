package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dtos.request.OrderRequestDTO;
import com.ecommerce.order_service.dtos.response.DataResponse;
import com.ecommerce.order_service.dtos.response.MessageResponse;
import com.ecommerce.order_service.dtos.response.SingleDataResponse;

public interface OrderService {

    MessageResponse placeOrder(OrderRequestDTO dto, Long loggedInUserId);

    SingleDataResponse getOrderById(Long orderId, Long loggedInUserId);

    DataResponse getOrdersByUser(Long loggedInUserId, int page, int size);

    MessageResponse cancelOrder(Long orderId, Long loggedInUserId);
}
