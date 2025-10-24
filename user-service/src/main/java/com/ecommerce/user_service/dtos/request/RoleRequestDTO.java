package com.ecommerce.user_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {

    @NotBlank(message = "Role name must not be blank")
    private String name;
}
