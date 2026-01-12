package com.learning.SpringSecurity.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank(message = "refreshToken is required")
    private String refreshToken;
}
