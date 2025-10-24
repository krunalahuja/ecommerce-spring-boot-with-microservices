# E-commerce Spring Boot Microservices

A **full-stack back-end e-commerce platform** built with **Spring Boot microservices architecture** using **Java 25**. The project is modular, scalable, and demonstrates real-world microservices communication, authentication, dynamic queries, and transactional operations. It uses **JWT-based security**, **email notifications for password resets**, and **CriteriaBuilder** for dynamic queries and pagination.

## Features

### Microservices Architecture
- **Auth Service**: Handles registration, login, JWT token generation, refresh tokens, and password reset emails. Tokens are securely generated and validated for API access.
- **User Service**: Manages user profiles, role-based access, and user-specific operations.
- **Product Service**: CRUD operations for products, stock management, and soft deletes with `isDeleted` flags.
- **Order Service**: Enables users to place orders, check status, and cancel orders. Uses **CriteriaBuilder** for dynamic queries and pagination of orders.
- **Wishlist Service**: Users can add/remove products to wishlists and order all or selected items directly from the wishlist.
- **Review Service**: Allows users to post, update, or delete product reviews. Supports fetching reviews by product or user with pagination.

### Security & Authentication
- **JWT Authentication**: All APIs are secured using JWT tokens. Tokens are validated in gateway and services to ensure secure microservice communication.
- **Password Reset via Email**: Generates one-time tokens for password reset. Users can update their passwords using secure token-based links.
- **Role-based Access Control**: Different endpoints secured according to user roles (e.g., admin vs. customer).

### API Gateway & Service Discovery
- **API Gateway**: Central entry point using **Spring Cloud Gateway**, routing requests to microservices.
- **Eureka Server**: Provides dynamic service registration and discovery, enabling flexible scaling and decoupled service communication.

### Database & Persistence
- Each service uses **PostgreSQL**.
- Soft deletes implemented using `isDeleted` flags.
- **CriteriaBuilder** used for dynamic filtering, search, and pagination in order and wishlist services.

### Microservice Communication
- **Feign Clients**: Services communicate via **OpenFeign**, ensuring type-safe and decoupled REST calls.

### Tech Stack
- Java 25 / Spring Boot 3.x  
- Spring Cloud (Eureka, Gateway, OpenFeign)  
- PostgreSQL  
- JWT for authentication  
- Maven  
