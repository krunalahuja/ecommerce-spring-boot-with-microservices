package com.ecommerce.user_service.service.Impl;

import com.ecommerce.user_service.dtos.request.SellerRequestDTO;
import com.ecommerce.user_service.dtos.request.UserRequestDTO;
import com.ecommerce.user_service.dtos.request.UserUpdateDTO;
import com.ecommerce.user_service.dtos.response.DataResponse;
import com.ecommerce.user_service.dtos.response.MessageResponse;
import com.ecommerce.user_service.dtos.response.SingleDataResponse;
import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.Seller;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.exception.UnauthorizedException;
import com.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.ecommerce.user_service.exception.UserNotFoundException;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.repository.SellerRepository;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SellerRepository sellerRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public MessageResponse createUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            throw new RuntimeException("USER role not found. Please initialize roles first.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRoles(Set.of(userRole));
        user.setIsDeleted(false);

        userRepository.save(user);
        return new MessageResponse(true, HttpStatus.OK, "User created successfully");
    }

    @Override
    public MessageResponse updateUser(Long userId, UserUpdateDTO dto, String loggedInUsername) {
        User user = getUserIfAuthorized(userId, loggedInUsername);

        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());

        userRepository.save(user);
        return new MessageResponse(true, HttpStatus.OK, "User updated successfully");
    }

    @Override
    public MessageResponse softDeleteUser(Long userId, String loggedInUsername) {
        User user = getUserIfAuthorized(userId, loggedInUsername);

        if (user.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already deleted");
        }

        user.setIsDeleted(true);
        userRepository.save(user);
        return new MessageResponse(true, HttpStatus.OK, "User soft deleted successfully");
    }

    @Override
    public MessageResponse restoreUser(Long userId, String loggedInUsername) {
        User user = getUserIfAuthorized(userId, loggedInUsername);

        if (!user.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not deleted");
        }

        user.setIsDeleted(false);
        userRepository.save(user);
        return new MessageResponse(true, HttpStatus.OK, "User restored successfully");
    }

    @Override
    public SingleDataResponse getUserById(Long userId, String loggedInUsername) {
        User user = getUserIfAuthorized(userId, loggedInUsername);
        return new SingleDataResponse(true, HttpStatus.OK, user);
    }

    @Override
    public DataResponse getAllUsers(int page, int size, String roleFilter, Boolean includeDeleted) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = cb.conjunction();

        if (roleFilter != null) {
            Join<User, Role> rolesJoin = root.join("roles", JoinType.INNER);
            predicate = cb.and(predicate, cb.equal(rolesJoin.get("name"), roleFilter));
        }

        if (includeDeleted != null && !includeDeleted) {
            predicate = cb.and(predicate, cb.equal(root.get("isDeleted"), false));
        }

        query.where(predicate);
        List<User> users = em.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class);
        countQuery.select(cb.count(countRoot)).where(predicate);
        long totalElements = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(users), totalElements, totalPages, page, size);
    }

    @Override
    public MessageResponse registerSeller(SellerRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        Role sellerRole = roleRepository.findByName("SELLER");
        if (sellerRole == null) {
            throw new RuntimeException("SELLER role not found. Please initialize roles first.");
        }

        // Create User
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setRoles(Set.of(sellerRole));
        user.setIsDeleted(false);
        userRepository.save(user);

        // Create Seller
        Seller seller = new Seller();
        seller.setUser(user);
        seller.setCompanyName(dto.getCompanyName());
        seller.setGstNumber(dto.getGstNumber());
        seller.setIsVerified(false);
        seller.setIsDeleted(false);
        sellerRepository.save(seller);

        return new MessageResponse(true, HttpStatus.OK, "Seller registered successfully");
    }

    @Override
    public SingleDataResponse getSellerById(Long sellerId, String loggedInUsername) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Seller not found with id: " + sellerId));

        if (seller.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller account is deleted");
        }

        if (!seller.getUser().getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        return new SingleDataResponse(true, HttpStatus.OK, seller);
    }

    @Override
    public DataResponse getAllSellers(int page, int size, Boolean includeDeleted) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Seller> query = cb.createQuery(Seller.class);
        Root<Seller> root = query.from(Seller.class);

        Predicate predicate = cb.conjunction();
        if (includeDeleted != null && !includeDeleted) {
            predicate = cb.and(predicate, cb.equal(root.get("isDeleted"), false));
        }

        query.where(predicate);
        List<Seller> sellers = em.createQuery(query)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Seller> countRoot = countQuery.from(Seller.class);
        countQuery.select(cb.count(countRoot)).where(predicate);
        long totalElements = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new DataResponse(true, HttpStatus.OK, Collections.singletonList(sellers), totalElements, totalPages, page, size);
    }

    @Override
    public MessageResponse softDeleteSeller(Long sellerId, String loggedInUsername) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Seller not found with id: " + sellerId));

        if (seller.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller already deleted");
        }

        if (!seller.getUser().getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        seller.setIsDeleted(true);
        sellerRepository.save(seller);
        return new MessageResponse(true, HttpStatus.OK, "Seller soft deleted successfully");
    }

    @Override
    public MessageResponse restoreSeller(Long sellerId, String loggedInUsername) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new UserNotFoundException("Seller not found with id: " + sellerId));

        if (!seller.getIsDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller is not deleted");
        }

        if (!seller.getUser().getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedException("Unauthorized access");
        }

        seller.setIsDeleted(false);
        sellerRepository.save(seller);
        return new MessageResponse(true, HttpStatus.OK, "Seller restored successfully");
    }

    /** Helper method to verify user authorization */
    private User getUserIfAuthorized(Long userId, String loggedInUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!user.getUsername().equals(loggedInUsername)) {
            throw new UnauthorizedException("Unauthorized");
        }

        return user;
    }
}
