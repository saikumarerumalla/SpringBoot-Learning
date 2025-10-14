package com.explore.ratelimiting.Ratelimiting.Controller;


import com.explore.ratelimiting.Ratelimiting.DTO.*;
import com.explore.ratelimiting.Ratelimiting.Service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RateLimiterController {

    @Autowired
    private RateLimiterService rateLimiterService;


    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse> placeOrder(@RequestParam String userId,
                                                    @RequestParam String restaurantId,
                                                    @RequestParam String items,
                                                    @RequestParam(defaultValue = "299.00") String amount) {

        if (rateLimiterService.isAllowedForOrderPlacement(userId)) {
            // Create successful order response using POJOs
            String orderId = "ORD_" + System.currentTimeMillis();
            OrderData orderData = OrderData.create(userId, restaurantId, items, "₹" + amount);

            OrderResponse response = OrderResponse.success(
                    orderId,
                    orderData,
                    rateLimiterService.getRemainingTokensPerUser(userId),
                    rateLimiterService.getRemainingTokensFoodOrder()
            );

            return ResponseEntity.ok(response);
        } else {
            // Check which limit was exceeded and create appropriate error response
            RateLimiterService.RateLimitStatus status = rateLimiterService.getRateLimitStatus(userId);

            ErrorDetails errorDetails;
            String errorMessage;

            if (status.getFoodOrderRemaining() == 0) {
                errorDetails = ErrorDetails.systemOverloaded();
                errorMessage = "System is at capacity. Too many orders being placed right now. Please try again later.";
            } else {
                errorDetails = ErrorDetails.userOrderLimitExceeded();
                errorMessage = "You're placing orders too quickly. Please wait before placing another order.";
            }

            OrderResponse response = OrderResponse.rateLimited(
                    errorMessage,
                    errorDetails,
                    status.getUserRemaining(),
                    status.getFoodOrderRemaining()
            );

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Retry-After", "60")
                    .body(response);
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPasswordEndpoint(@RequestParam String email) {

        if (rateLimiterService.isAllowedForForgotPassword()) {
            // Create successful password reset response using POJOs
            PasswordResetData resetData = PasswordResetData.create(email, null);

            ForgotPasswordResponse response = ForgotPasswordResponse.success(
                    resetData,
                    rateLimiterService.getRemainingTokensForgotPassword(),
                    null // No global capacity for forgot password
            );

            return ResponseEntity.ok(response);
        } else {
            // Rate limited - too many forgot password attempts
            ErrorDetails errorDetails = ErrorDetails.passwordResetRateLimited();
            String errorMessage = "Too many password reset attempts. Please try again later.";

            ForgotPasswordResponse response = ForgotPasswordResponse.rateLimited(
                    errorMessage,
                    errorDetails,
                    rateLimiterService.getRemainingTokensForgotPassword(),
                    null // No global capacity for forgot password
            );

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("X-Retry-After", "60")
                    .body(response);
        }
    }
}
