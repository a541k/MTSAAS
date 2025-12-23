package com.mtsaas.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "Username mandatory")
    private String username;

    @NotBlank(message = "Password mandatory")
    private String password;
}
