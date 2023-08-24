package com.practice.boxauthservice.security.handller;

import com.practice.boxauthservice.global.env.EnvUtil;
import com.practice.boxauthservice.global.jwt.JwtUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;
  private final EnvUtil envUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    try {
      String nickname = getNickname(authentication);
      String jwtToken = jwtUtil.generateAccessJwtToken(nickname);
      Cookie jwtCookie = generateJwtCookie(jwtToken);
      parseResponse(response, jwtCookie);
    } catch (Exception e) {
      throw new OAuth2AuthenticationException("Authentication failed");
    }
  }

  private String getNickname(Authentication authentication) {
    String clientRegistration = ((OAuth2AuthenticationToken) authentication)
        .getAuthorizedClientRegistrationId();
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    Map<String, Object> userInfo = oAuth2User.getAttributes();
    if (clientRegistration.equals("42api")) {
      return String.valueOf(userInfo.get("login"));
    } else {
      throw new OAuth2AuthenticationException("ClientRegistration Wrong!");
    }
  }

  private Cookie generateJwtCookie(String jwtToken) {
    Cookie jwtCookie = new Cookie("box-jwt", jwtToken);
    int cookieAge = Integer.parseInt(envUtil.getEnv("jwt.token.ACCESS_EXPIRATION_TIME")) / 1000;
    jwtCookie.setHttpOnly(true);
    jwtCookie.setMaxAge(cookieAge);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    return jwtCookie;
  }

  private void parseResponse(HttpServletResponse response, Cookie jwtCookie) throws IOException {
    response.setStatus(302);
    response.setHeader("Location", envUtil.getEnv("header.auth-redirect-location.value"));
    response.addCookie(jwtCookie);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.resetBuffer();
  }
}
