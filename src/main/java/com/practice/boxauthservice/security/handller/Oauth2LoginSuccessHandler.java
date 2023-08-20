package com.practice.boxauthservice.security.handller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
      OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
      Map<String, Object> userInfo = parseUserInfo(oAuth2User);
      Long userId = Long.valueOf(userInfo.get("userId").toString());
      String jwtToken = jwtUtil.generateAccessJwtToken(userId);
      Cookie jwtCookie = generateJwtCookie(jwtToken);
      parseResponse(response, jwtCookie, jwtToken, userInfo);
    } catch (Exception e) {
      throw new OAuth2AuthenticationException("Authentication failed");
    }
  }

  private Map<String, Object> parseUserInfo(OAuth2User oAuth2User) {
    Object rawUserInfo = oAuth2User.getAttributes().get("user-info");
    if (rawUserInfo instanceof Map) {
      return (Map<String, Object>) rawUserInfo;
    } else {
      throw new RuntimeException();
    }
  }

  private Cookie generateJwtCookie(String jwtToken) {
    Cookie jwtCookie = new Cookie("jwt", jwtToken);
    int cookieAge = Integer.parseInt(envUtil.getEnv("jwt.token.ACCESS_EXPIRATION_TIME")) / 1000;
    jwtCookie.setHttpOnly(true);
    jwtCookie.setMaxAge(cookieAge);
    jwtCookie.setSecure(true);
    jwtCookie.setPath("/");
    return jwtCookie;
  }

  private void parseResponse(HttpServletResponse response, Cookie jwtCookie, String jwtToken,
    Map<String, Object> userInfo) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(userInfo);
    response.addCookie(jwtCookie);
    response.setHeader("Authorization", "Bearer " + jwtToken);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.resetBuffer();
    response.getWriter().write(json);
  }
}
