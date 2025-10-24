package com.ecommerce.auth_service.config.Jwtconfig;

import com.ecommerce.auth_service.dtos.MessageResponse;
import com.ecommerce.auth_service.service.JwtService;
import com.ecommerce.auth_service.service.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        response.setContentType("application/json");
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (!tokenBlacklistService.isTokenBlacklisted(token)) {

                    String email = jwtService.getEmail(token);
                    List<String> roles = jwtService.getRoles(token);

                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // Build Spring Security user details from JWT
                        UserDetails userDetails = User.builder()
                                .username(email)
                                .password("") // password not needed
                                .authorities(roles.stream().map(r -> "ROLE_" + r).toArray(String[]::new))
                                .build();

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    MessageResponse resBody = new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Token is blacklisted");
                    response.getWriter().write(objectMapper.writeValueAsString(resBody));
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("Authentication error: {}", ex.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            MessageResponse resBody = new MessageResponse(false, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            response.getWriter().write(objectMapper.writeValueAsString(resBody));
        }
    }
}
