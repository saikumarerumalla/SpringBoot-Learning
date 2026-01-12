package com.learning.SpringSecurity.Model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotBlank(message = "Refresh Token is required")
    private String refreshToken;
}
