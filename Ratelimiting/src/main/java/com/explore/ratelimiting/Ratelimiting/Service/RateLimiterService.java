package com.explore.ratelimiting.Ratelimiting.Service;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Bucket foodOrderRateLimiter;
    private final Bucket forgotPasswordRateLimiter;

    // Thread-safe storage for user-specific rate limiting buckets
    // Each userId gets their own bucket with 5 requests per minute limit
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public RateLimiterService(
            @Qualifier("foodOrderRateLimiter") Bucket foodOrderRateLimiter,
            @Qualifier("forgotPasswordRateLimiter") Bucket forgotPasswordRateLimiter
    ) {
        this.foodOrderRateLimiter = foodOrderRateLimiter;
        this.forgotPasswordRateLimiter = forgotPasswordRateLimiter;
    }

    public boolean isAllowedFoodOrder() {
        return foodOrderRateLimiter.tryConsume(1);
    }

    public boolean isAllowedPerUser(String userId) {
        Bucket userBucket = userBuckets.computeIfAbsent(userId,
                k -> Bucket.builder()
                        .addLimit(Bandwidth.classic(5,
                                Refill.intervally(5, java.time.Duration.ofMinutes(1))))
                        .build());
        return userBucket.tryConsume(1);
    }

    public boolean isAllowedForOrderPlacement(String userId) {
        // Check food order system capacity first (fail fast if system is overloaded)
        if (!foodOrderRateLimiter.tryConsume(1)) {
            return false;
        }

        // Check per-user limit
        if (!isAllowedPerUser(userId)) {
            return false; // User limit exceeded - user placing orders too quickly
        }

        return true; // Both limits passed - order can be placed
    }

    public boolean isAllowedForForgotPassword() {
        return forgotPasswordRateLimiter.tryConsume(1);
    }

    /**
     * Get detailed consumption information for food order rate limiter
     */
    public ConsumptionProbe getConsumptionProbeFoodOrder() {
        return foodOrderRateLimiter.tryConsumeAndReturnRemaining(1);
    }

    public ConsumptionProbe getConsumptionProbePerUser(String userId) {
        Bucket userBucket = userBuckets.computeIfAbsent(userId,
                k -> Bucket.builder()
                        .addLimit(io.github.bucket4j.Bandwidth.classic(5,
                                io.github.bucket4j.Refill.intervally(5, java.time.Duration.ofMinutes(1))))
                        .build());
        return userBucket.tryConsumeAndReturnRemaining(1);
    }

    public long getRemainingTokensFoodOrder() {
        return foodOrderRateLimiter.getAvailableTokens();
    }

    public long getRemainingTokensPerUser(String userId) {
        Bucket userBucket = userBuckets.get(userId);
        return userBucket != null ? userBucket.getAvailableTokens() : 5; // Return max if user hasn't made requests yet
    }

    public long getRemainingTokensForgotPassword() {
        return forgotPasswordRateLimiter.getAvailableTokens();
    }

    public RateLimitStatus getRateLimitStatus(String userId) {
        return new RateLimitStatus(
                getRemainingTokensFoodOrder(),
                getRemainingTokensPerUser(userId),
                userBuckets.containsKey(userId)
        );
    }

    /**
     * Reset user's rate limit (for testing purposes)
     */
    public void resetUserRateLimit(String userId) {
        userBuckets.remove(userId);
    }

    public Map<String, Bucket> getAllUserBuckets() {
        return Map.copyOf(userBuckets);
    }

    public static class RateLimitStatus {
        private final long foodOrderRemaining;
        private final long userRemaining;
        private final boolean userHasBucket;

        public RateLimitStatus(long foodOrderRemaining, long userRemaining, boolean userHasBucket) {
            this.foodOrderRemaining = foodOrderRemaining;
            this.userRemaining = userRemaining;
            this.userHasBucket = userHasBucket;
        }

        // Getters
        public long getFoodOrderRemaining() {
            return foodOrderRemaining;
        }

        public long getUserRemaining() {
            return userRemaining;
        }

        public boolean isUserHasBucket() {
            return userHasBucket;
        }

    }

}
