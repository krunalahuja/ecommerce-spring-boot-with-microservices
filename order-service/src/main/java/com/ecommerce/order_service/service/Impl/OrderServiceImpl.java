package com.ecommerce.order_service.service.Impl;

import com.ecommerce.order_service.dtos.request.OrderRequestDTO;
import com.ecommerce.order_service.dtos.response.*;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.exception.ProductNotFoundException;
import com.ecommerce.order_service.exception.UserNotFoundException;
import com.ecommerce.order_service.feign.ProductClient;
import com.ecommerce.order_service.feign.UserClient;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    @PersistenceContext
    private EntityManager em;

    @Override
    public MessageResponse placeOrder(OrderRequestDTO dto, Long loggedInUserId) {
        // Fetch user via Feign
        UserResponse user = userClient.getUserById(loggedInUserId);
        if (user == null) throw new UserNotFoundException(loggedInUserId);

        // Fetch product via Feign
        ProductResponse product = productClient.getProductById(dto.getProductId());
        if (product == null || product.getIsDeleted() != null && product.getIsDeleted())
            throw new ProductNotFoundException(dto.getProductId());

        if (product.getStock() < dto.getQuantity()) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        // Deduct stock via Feign (you may implement a ProductService endpoint for stock update)
        product.setStock(product.getStock() - dto.getQuantity());

        // Calculate total
        double total = product.getPrice() * dto.getQuantity();

        // Save order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setProductId(product.getId());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(total);
        order.setStatus("PLACED");
        order.setIsDeleted(false);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        return new MessageResponse(true, HttpStatus.CREATED, "Order placed successfully");
    }

    @Override
    public SingleDataResponse getOrderById(Long orderId, Long loggedInUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserId().equals(loggedInUserId)) {
            return new SingleDataResponse(false, HttpStatus.UNAUTHORIZED, "Unauthorized access");
        }

        if (order.getIsDeleted()) {
            return new SingleDataResponse(false, HttpStatus.NOT_FOUND, "Order was cancelled");
        }

        return new SingleDataResponse(true, HttpStatus.OK, order);
    }

    @Override
    public DataResponse getOrdersByUser(Long loggedInUserId, int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> query = cb.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        Predicate predicate = cb.equal(root.get("userId"), loggedInUserId);
        predicate = cb.and(predicate, cb.equal(root.get("isDeleted"), false));

        query.where(predicate);

        List<Order> orders = em.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Order> countRoot = countQuery.from(Order.class);
        countQuery.select(cb.count(countRoot)).where(predicate);
        long totalElements = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(orders),
                totalElements, totalPages, page, size);
    }

    @Override
    public MessageResponse cancelOrder(Long orderId, Long loggedInUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!order.getUserId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (order.getIsDeleted()) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Order already cancelled");
        }

        order.setIsDeleted(true);
        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());

        // Restore stock via Feign (implement endpoint in ProductService)
        ProductResponse product = productClient.getProductById(order.getProductId());
        product.setStock(product.getStock() + order.getQuantity());

        orderRepository.save(order);
        return new MessageResponse(true, HttpStatus.OK, "Order cancelled successfully");
    }
}
