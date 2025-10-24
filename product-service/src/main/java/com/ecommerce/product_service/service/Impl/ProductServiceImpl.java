package com.ecommerce.product_service.service.Impl;

import com.ecommerce.product_service.dtos.request.ProductRequestDTO;
import com.ecommerce.product_service.dtos.request.ProductUpdateDTO;
import com.ecommerce.product_service.dtos.response.*;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.ProductNotFoundException;
import com.ecommerce.product_service.feign.SellerClient;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SellerClient sellerClient;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create a new product (only sellers can create)
     */
    @Override
    public MessageResponse createProduct(ProductRequestDTO dto, Long loggedInUserId) {
        UserResponse sellerResponse = sellerClient.getSellerById(loggedInUserId);

        // Validate seller role
        if (sellerResponse == null || !"SELLER".equalsIgnoreCase(sellerResponse.getRole())) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Only sellers can create products");
        }

        // Prevent duplicate product name for the same seller
        if (productRepository.existsByNameAndSellerId(dto.getName(), sellerResponse.getId())) {
            return new MessageResponse(false, HttpStatus.BAD_REQUEST, "Product already exists for this seller");
        }

        // Create and save product
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setCompany(dto.getCompany());
        product.setSellerId(sellerResponse.getId());
        product.setIsDeleted(false);

        productRepository.save(product);
        return new MessageResponse(true, HttpStatus.CREATED, "Product created successfully");
    }

    /**
     * Update product (only by its seller)
     */
    @Override
    public MessageResponse updateProduct(Long productId, ProductUpdateDTO dto, Long loggedInUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getSellerId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "You can update only your own products");
        }

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getCompany() != null) product.setCompany(dto.getCompany());

        productRepository.save(product);
        return new MessageResponse(true, HttpStatus.OK, "Product updated successfully");
    }

    /**
     * Soft delete product
     */
    @Override
    public MessageResponse softDeleteProduct(Long productId, Long loggedInUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getSellerId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "You can delete only your own products");
        }

        product.setIsDeleted(true);
        productRepository.save(product);

        return new MessageResponse(true, HttpStatus.OK, "Product soft-deleted successfully");
    }

    /**
     * Restore product
     */
    @Override
    public MessageResponse restoreProduct(Long productId, Long loggedInUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getSellerId().equals(loggedInUserId)) {
            return new MessageResponse(false, HttpStatus.UNAUTHORIZED, "You can restore only your own products");
        }

        product.setIsDeleted(false);
        productRepository.save(product);

        return new MessageResponse(true, HttpStatus.OK, "Product restored successfully");
    }

    /**
     * Get product by ID
     */
    @Override
    public SingleDataResponse getProductById(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty() || Boolean.TRUE.equals(productOpt.get().getIsDeleted())) {
            return new SingleDataResponse(false, HttpStatus.NOT_FOUND, "Product not found");
        }

        return new SingleDataResponse(true, HttpStatus.OK, productOpt.get());
    }

    /**
     * Get all products with filters using CriteriaBuilder (efficient query)
     */
    @Override
    public DataResponse getAllProducts(int page, int size, String category, Boolean includeDeleted) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filter by category
        if (category != null && !category.isBlank()) {
            predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
        }

        // Exclude deleted products unless explicitly included
        if (includeDeleted == null || !includeDeleted) {
            predicates.add(cb.isFalse(root.get("isDeleted")));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("id"))); // Sort by ID

        TypedQuery<Product> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Product> products = query.getResultList();

        // Count total records
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot)).where(predicates.toArray(new Predicate[0]));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(products), totalCount.intValue(),
                (int) Math.ceil((double) totalCount / size), page, size);
    }

    /**
     * Get all products by a specific seller using CriteriaBuilder
     */
    @Override
    public DataResponse getProductsBySeller(Long sellerId, int page, int size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Predicate sellerPredicate = cb.equal(root.get("sellerId"), sellerId);
        Predicate notDeleted = cb.isFalse(root.get("isDeleted"));
        cq.where(cb.and(sellerPredicate, notDeleted));
        cq.orderBy(cb.asc(root.get("id")));

        TypedQuery<Product> query = entityManager.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Product> products = query.getResultList();

        // Count total records
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot))
                .where(cb.and(cb.equal(countRoot.get("sellerId"), sellerId),
                        cb.isFalse(countRoot.get("isDeleted"))));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(products), totalCount.intValue(),
                (int) Math.ceil((double) totalCount / size), page, size);
    }
}
