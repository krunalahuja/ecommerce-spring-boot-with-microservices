package com.ecommerce.order_service.service.Impl;

import com.ecommerce.order_service.dtos.request.OrderRequestDTO;
import com.ecommerce.order_service.dtos.request.WishlistRequestDTO;
import com.ecommerce.order_service.dtos.response.*;
import com.ecommerce.order_service.entity.Wishlist;
import com.ecommerce.order_service.exception.ProductNotFoundException;
import com.ecommerce.order_service.exception.UserNotFoundException;
import com.ecommerce.order_service.exception.WishlistNotFoundException;
import com.ecommerce.order_service.feign.ProductClient;
import com.ecommerce.order_service.feign.UserClient;
import com.ecommerce.order_service.repository.WishlistRepository;
import com.ecommerce.order_service.service.WishlistService;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderService orderService;

    /** Helper: Convert string to set of product IDs */
    private Set<Long> parseProductIds(String productIds) {
        if (productIds == null || productIds.isEmpty()) return new HashSet<>();
        return Arrays.stream(productIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
    }

    /** Helper: Convert set of product IDs to string */
    private String stringifyProductIds(Set<Long> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    /** Helper: fetch wishlist or create new */
    private Wishlist getOrCreateWishlist(Long loggedInUserId) {
        return wishlistRepository.findByUserId(loggedInUserId)
                .orElseGet(() -> {
                    Wishlist w = new Wishlist();
                    w.setUserId(loggedInUserId);
                    w.setProductIds("");
                    w.setIsDeleted(false);
                    w.setCreatedAt(LocalDateTime.now());
                    return w;
                });
    }

    @Override
    public MessageResponse addProductToWishlist(WishlistRequestDTO dto, Long loggedInUserId) {
        // Validate user
        if (userClient.getUserById(loggedInUserId) == null) throw new UserNotFoundException(loggedInUserId);

        // Validate product
        ProductResponse product = productClient.getProductById(dto.getProductId());
        if (product == null || Boolean.TRUE.equals(product.getIsDeleted())) throw new ProductNotFoundException(dto.getProductId());

        Wishlist wishlist = getOrCreateWishlist(loggedInUserId);
        Set<Long> productSet = parseProductIds(wishlist.getProductIds());

        if (!productSet.add(product.getId())) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Product already in wishlist");
        }

        wishlist.setProductIds(stringifyProductIds(productSet));
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);

        return new MessageResponse(true, HttpStatus.OK, "Product added to wishlist successfully");
    }

    @Override
    public MessageResponse removeProductFromWishlist(Long productId, Long loggedInUserId) {
        Wishlist wishlist = wishlistRepository.findByUserId(loggedInUserId)
                .orElseThrow(() -> new WishlistNotFoundException(loggedInUserId));

        Set<Long> productSet = parseProductIds(wishlist.getProductIds());
        if (!productSet.remove(productId)) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Product not in wishlist");
        }

        wishlist.setProductIds(stringifyProductIds(productSet));
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);

        return new MessageResponse(true, HttpStatus.OK, "Product removed from wishlist successfully");
    }

    @Override
    public DataResponse getWishlistByUser(Long loggedInUserId) {
        Wishlist wishlist = wishlistRepository.findByUserId(loggedInUserId)
                .orElseThrow(() -> new WishlistNotFoundException(loggedInUserId));

        Set<Long> productIds = parseProductIds(wishlist.getProductIds());
        if (productIds.isEmpty()) {
            return new DataResponse(true, HttpStatus.OK, Collections.emptyList(), 0, 1, 0, 0);
        }

        List<ProductResponse> products = productIds.stream()
                .map(productClient::getProductById)
                .filter(p -> p != null && !Boolean.TRUE.equals(p.getIsDeleted()))
                .collect(Collectors.toList());

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(products), products.size(), 1, 0, products.size());
    }

    @Override
    public MessageResponse clearWishlist(Long loggedInUserId) {
        Wishlist wishlist = wishlistRepository.findByUserId(loggedInUserId)
                .orElseThrow(() -> new WishlistNotFoundException(loggedInUserId));

        wishlist.setProductIds("");
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);

        return new MessageResponse(true, HttpStatus.OK, "Wishlist cleared successfully");
    }

    @Override
    public MessageResponse orderFromWishlist(Set<Long> productIds, Long loggedInUserId) {
        Wishlist wishlist = wishlistRepository.findByUserId(loggedInUserId)
                .orElseThrow(() -> new WishlistNotFoundException(loggedInUserId));

        Set<Long> wishlistProducts = parseProductIds(wishlist.getProductIds());
        if (wishlistProducts.isEmpty()) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Wishlist is empty");
        }

        // Decide which products to order
        Set<Long> toOrder = (productIds == null || productIds.isEmpty())
                ? new HashSet<>(wishlistProducts)
                : productIds.stream().filter(wishlistProducts::contains).collect(Collectors.toSet());

        if (toOrder.isEmpty()) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "No valid products selected to order");
        }

        // Place orders
        for (Long productId : toOrder) {
            OrderRequestDTO orderDto = new OrderRequestDTO();
            orderDto.setProductId(productId);
            orderDto.setQuantity(1); // default 1
            orderService.placeOrder(orderDto, loggedInUserId);
        }

        // Remove ordered products from wishlist
        wishlistProducts.removeAll(toOrder);
        wishlist.setProductIds(stringifyProductIds(wishlistProducts));
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);

        return new MessageResponse(true, HttpStatus.OK, "Wishlist products ordered successfully");
    }
}
