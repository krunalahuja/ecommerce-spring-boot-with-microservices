package com.ecommerce.user_service.dtos.response;

import com.ecommerce.user_service.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private Address address;
    private String companyName;
    private String gstNumber;
    private Set<String> roles;
}
