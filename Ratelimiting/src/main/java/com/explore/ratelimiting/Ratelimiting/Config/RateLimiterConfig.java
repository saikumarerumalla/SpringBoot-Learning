package com.explore.ratelimiting.Ratelimiting.Config;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfig {

    @Bean("foodOrderRateLimiter")
    public  Bucket foodOrderRateLimiter() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofMinutes(1))))
                .build();
    }

    @Bean("perUserRateLimiter")
    public Bucket perUserRateLimiter(){
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }

    @Bean("forgotPasswordRateLimiter")
    public Bucket forgotPasswordRateLimiter() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1))))
                .build();

    }

}
