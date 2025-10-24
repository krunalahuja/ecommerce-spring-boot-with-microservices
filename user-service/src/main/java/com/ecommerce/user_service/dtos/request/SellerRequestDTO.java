package com.ecommerce.user_service.dtos.request;

import com.ecommerce.user_service.entity.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class SellerRequestDTO {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @NotBlank(message = "Phone number must not be blank")
    private String phone;

    @Valid
    private Address address;

    @NotBlank(message = "Company name must not be blank")
    private String companyName;

    @NotBlank(message = "GST number must not be blank")
    private String gstNumber;
}
