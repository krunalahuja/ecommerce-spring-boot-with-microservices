# E-commerce Spring Boot Microservices

A **full-stack e-commerce platform** built with **Spring Boot microservices** architecture using **Java 25**. The project is modular, scalable, and demonstrates real-world microservices communication, dynamic queries, and database interactions.

## Features

- **Microservices Architecture**:
  - **Auth Service**: Handles user authentication and authorization.
  - **User Service**: Manages user profiles and data.
  - **Product Service**: Manages products, inventory, and details.
  - **Order Service**: Handles order placement, tracking, and management.
  - **Wishlist Service**: Users can maintain wishlists and order selectively or all products.
  - **Review Service**: Users can add, update, and delete product reviews.

- **API Gateway**:
  - Centralized entry point for all microservices using **Spring Cloud Gateway**.

- **Service Discovery**:
  - Services registered with **Eureka Server** for dynamic service discovery.

- **Database**:
  - Each service uses **PostgreSQL** for persistence.
  - **CriteriaBuilder** used for dynamic queries, filtering, and pagination in order and wishlist services.

- **Feign Clients**:
  - Microservices communicate via **Feign REST clients** for simplicity and decoupling.

- **Best Practices**:
  - DTOs for request/response separation.
  - Soft deletes with `isDeleted` and audit fields (`createdAt`, `updatedAt`).
  - Paginated responses for large datasets.

## Tech Stack

- Java 25 / Spring Boot 3.x  
- Spring Cloud (Eureka, Gateway, OpenFeign)  
- PostgreSQL  
- Maven  
