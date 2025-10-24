package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dtos.request.OrderRequestDTO;
import com.ecommerce.order_service.dtos.response.DataResponse;
import com.ecommerce.order_service.dtos.response.MessageResponse;
import com.ecommerce.order_service.dtos.response.SingleDataResponse;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<MessageResponse> placeOrder(@RequestBody OrderRequestDTO dto,
                                                      @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(orderService.placeOrder(dto, loggedInUserId), HttpStatus.OK);
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<SingleDataResponse> getOrderById(@PathVariable Long orderId,
                                                           @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId, loggedInUserId), HttpStatus.OK);
    }

    // Get all orders for the logged-in user
    @GetMapping("/user")
    public ResponseEntity<DataResponse> getOrdersByUser(@RequestParam Long loggedInUserId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(orderService.getOrdersByUser(loggedInUserId, page, size), HttpStatus.OK);
    }

    // Cancel an order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<MessageResponse> cancelOrder(@PathVariable Long orderId,
                                                       @RequestParam Long loggedInUserId) {
        return new ResponseEntity<>(orderService.cancelOrder(orderId, loggedInUserId), HttpStatus.OK);
    }
}
