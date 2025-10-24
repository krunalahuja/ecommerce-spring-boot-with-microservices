package com.ecommerce.auth_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserServiceFeignClient {

    // Validate credentials
    @PostMapping("/users/validate-credentials")
    boolean validateCredentials(@RequestParam("email") String email,
                                @RequestParam("password") String password);

    // Fetch roles of the user
    @GetMapping("/users/{email}/roles")
    List<String> getRolesByEmail(@PathVariable("email") String email);
}
