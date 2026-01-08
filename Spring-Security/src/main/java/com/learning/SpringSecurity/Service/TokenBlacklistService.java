package com.learning.SpringSecurity.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public void blacklistToken(String token){
        try{
            Date expirationDate = jwtService.extractExpiration(token);
            long ttl = expirationDate.getTime() - System.currentTimeMillis();
            if(ttl > 0){
                redisTemplate.opsForValue().set(
                        "blacklist:"+token,
                        "revoked",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        }catch (Exception e){
            redisTemplate.opsForValue().set(
                    "blacklist:"+token,
                    "revoked",
                    24,
                    TimeUnit.HOURS
            );
        }
    }

    public boolean isBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:"+token));
    }

}
