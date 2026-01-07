package com.learning.SpringSecurity.Controller;

import com.learning.SpringSecurity.Dao.RefreshToken;
import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Model.*;
import com.learning.SpringSecurity.Repo.UserRepo;
import com.learning.SpringSecurity.Service.AuthService;
import com.learning.SpringSecurity.Service.JwtService;
import com.learning.SpringSecurity.Service.TokenBlacklistService;
import com.learning.SpringSecurity.Service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest){
        AuthResponse response = authService.register(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("csrf-token")
    public CsrfToken csrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest){
        AuthResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request){
        RefreshToken refreshToken = tokenService.verifyRefreshToken(request.getRefreshToken());
        String newAccessToken = tokenService.generateAccessToken(refreshToken.getUser());

        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout (@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody(required = false) LogoutRequest request){
        String accessToken = authHeader.substring(7);
        blacklistService.blacklistToken(accessToken);

        if(request != null && request.getRefreshToken() != null){
            tokenService.revokeRefreshToken(request.getRefreshToken());
        }

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out Successfully"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<MessageResponse> logout (@RequestHeader("Authorization") String authHeader){
        String accessToken = authHeader.substring(7);
        String username = jwtService.extractUserName(accessToken);

        User user = userRepo.findByUsername(username)
                .orElseThrow( ()-> new UsernameNotFoundException("User not found"));
        blacklistService.blacklistToken(accessToken);
        tokenService.revokeAllUserTokens(user);

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new MessageResponse("Logged out from all devices Successfully"));
    }







}
