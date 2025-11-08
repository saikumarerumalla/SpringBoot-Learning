package com.explore.ratelimiting.Ratelimiting.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForgotPasswordResponse {

    private boolean success;
    private String message;
    private PasswordResetData data;
    private ErrorDetails error;
    private Long remainingAttempts;
    private Long globalCapacity;
    private String status;



    public ForgotPasswordResponse(boolean success, String message, String status) {
        this.success = success;
        this.message = message;
        this.status = status;
    }


    public static ForgotPasswordResponse success(PasswordResetData data, Long remainingAttempts, Long globalCapacity) {
        ForgotPasswordResponse response = new ForgotPasswordResponse(true, "Password reset link sent to your email", "SUCCESS");
        response.setData(data);
        response.setRemainingAttempts(remainingAttempts);
        response.setGlobalCapacity(globalCapacity);
        return response;
    }


    public static ForgotPasswordResponse rateLimited(String message, ErrorDetails error, Long remainingAttempts, Long globalCapacity) {
        ForgotPasswordResponse response = new ForgotPasswordResponse(false, message, "RATE_LIMITED");
        response.setError(error);
        response.setRemainingAttempts(remainingAttempts);
        response.setGlobalCapacity(globalCapacity);
        return response;
    }

}
