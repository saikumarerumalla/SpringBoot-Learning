package com.learning.SpringSecurity.Utility;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
@Component
public class JwtProperties {
    private Long accessTokenValidity;
    private Long refreshTokenValidity;
}
