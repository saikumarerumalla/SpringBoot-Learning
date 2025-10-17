package com.explore.ratelimiting.Ratelimiting.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ErrorDetails {

    private String code;
    private String type;
    private String details;
    private String suggestion;

    public static ErrorDetails systemOverloaded() {
        return new ErrorDetails(
                "SYSTEM_OVERLOADED",
                "TooManyOrders",
                "System is at capacity. Too many orders being placed right now. Please try again later.",
                "Our kitchen is busy! Try again in a minute."
        );
    }

    public static ErrorDetails userOrderLimitExceeded() {
        return new ErrorDetails(
                "USER_ORDER_LIMIT_EXCEEDED",
                "TooManyOrders",
                "You're placing orders too quickly. Please wait before placing another order.",
                "Take a moment to review your order, then try again."
        );
    }

    public static ErrorDetails userRateLimited() {
        return new ErrorDetails(
                "USER_RATE_LIMITED",
                "TooManyRequests",
                "Too many password reset attempts. Please try again later.",
                "Please wait before trying again."
        );
    }

    public static ErrorDetails passwordResetRateLimited() {
        return new ErrorDetails(
                "RATE_LIMITED",
                "TooManyRequests",
                "You have exceeded the maximum number of password reset attempts. Please wait before trying again.",
                "Please wait before trying again."
        );
    }

}
