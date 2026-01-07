package com.learning.SpringSecurity.Service;

import com.learning.SpringSecurity.Dao.Role;
import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Model.AuthResponse;
import com.learning.SpringSecurity.Model.LoginRequest;
import com.learning.SpringSecurity.Model.RegisterRequest;
import com.learning.SpringSecurity.Repo.UserRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;


    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest){
        if(userRepo.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");
        roles.add(userRole);

        user.setRoles(roles);
        user = userRepo.save(user);

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        tokenService.saveRefreshToken(user, refreshToken, httpRequest.getHeader("User-Agent"));

        return new AuthResponse(accessToken, refreshToken, "Bearer", user.getFullName(), user.getEmail());
    }

    @Transactional
    public AuthResponse login(@Valid LoginRequest request, HttpServletRequest httpRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(()-> new RuntimeException("User not found"));

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        tokenService.saveRefreshToken(user, refreshToken, httpRequest.getHeader("User-Agent"));

        return new AuthResponse(
                accessToken, refreshToken, "Bearer", user.getFullName(), user.getEmail()
        );
    }
}
