package com.practice.boxauthservice.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.practice.boxauthservice.global.env.EnvUtil;
import java.util.Date;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final EnvUtil envUtil;

  public String generateAccessJwtToken(Long userId) {
    int expirationTime = Integer.parseInt(envUtil.getEnv("jwt.token.ACCESS_EXPIRATION_TIME"));
    String secret = envUtil.getEnv("jwt.token.ACCESS_SECRET");

    return JWT.create().withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
      .withClaim("userId", userId).sign(Algorithm.HMAC512(Objects.requireNonNull(secret)));
  }
}
