package com.ecommerce.user_service.dtos.request;

import com.ecommerce.user_service.entity.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    private String fullName;

    private String phone;

    @Valid
    private Address address;

    private String username;
}
