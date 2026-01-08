package com.learning.SpringSecurity.Service;

import com.learning.SpringSecurity.Dao.RefreshToken;
import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Exception.InvalidTokenException;
import com.learning.SpringSecurity.Repo.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(User user){
        return jwtService.generateAccessToken(user);
    }

    public String generateRefreshToken(User user){
        return jwtService.generateRefreshToken(user);
    }

    @Transactional
    public RefreshToken saveRefreshToken(User user, String token, String userAgent){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(604800000L));
        refreshToken.setUserAgent(userAgent);
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(()-> new InvalidTokenException("Refresh token not found"));

        if(refreshToken.isRevoked()){
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            throw new InvalidTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token){
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken ->{
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(Instant.now());
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Transactional
    public void revokeAllUserTokens(User user){
        refreshTokenRepository.revokeAllUserTokens(user);
    }


}
