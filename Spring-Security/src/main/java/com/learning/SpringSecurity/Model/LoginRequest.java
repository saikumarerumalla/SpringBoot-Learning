package com.learning.SpringSecurity.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequest {

    @NotBlank(message = "UserName is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;
}
