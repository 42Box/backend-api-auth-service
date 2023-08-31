package com.practice.boxauthservice.security.handller;

import com.practice.boxauthservice.global.env.EnvUtil;
import com.practice.boxauthservice.global.jwt.JwtUtil;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
      String uuid = getUserInfoElement(authentication, "uuid");
      String role = getUserInfoElement(authentication, "role");
      String profileImagePath = getUserInfoElement(authentication, "profileImagePath");
      String profileImageUrl = getUserInfoElement(authentication, "profileImageUrl");

      String jwtToken = jwtUtil.generateAccessJwtToken(nickname, uuid, role, profileImagePath,
          profileImageUrl);
      ResponseCookie jwtCookie = generateJwtCookie(jwtToken);
      parseResponse(response, jwtCookie, jwtToken);
    } catch (Exception e) {
      throw new OAuth2AuthenticationException("Authentication failed");
    }
  }

  private String getUserInfoElement(Authentication authentication, String element) {
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    Map<String, Object> userInfo = (Map<String, Object>) oAuth2User.getAttributes()
        .get("user-info");
    return String.valueOf(userInfo.get(element));
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

  private ResponseCookie generateJwtCookie(String jwtToken) {
    ResponseCookie jwtCookie = ResponseCookie.from(envUtil.getEnv("jwt.token.AUTH_TOKEN_NAME"),
            jwtToken)
        .maxAge(Integer.parseInt(envUtil.getEnv("jwt.token.ACCESS_EXPIRATION_TIME")) / 1000)
        .httpOnly(true)
        .path("/")
        .secure(true)
        .sameSite("None")
        .build();
    return jwtCookie;
  }

//  private Cookie generateJwtCookie(String jwtToken) {
//    Cookie jwtCookie = new Cookie(envUtil.getEnv("jwt.token.AUTH_TOKEN_NAME"), jwtToken);
//    int cookieAge = Integer.parseInt(envUtil.getEnv("jwt.token.ACCESS_EXPIRATION_TIME")) / 1000;
//    jwtCookie.setHttpOnly(true);
//    jwtCookie.setMaxAge(cookieAge);
//    jwtCookie.setSecure(true);
//    jwtCookie.setPath("/");
//    return jwtCookie;
//  }

  private void parseResponse(HttpServletResponse response, ResponseCookie jwtCookie, String jwt)
      throws IOException {
    response.setStatus(302);
    response.setHeader("Authorization", envUtil.getEnv("jwt.token.TOKEN_PREFIX") + " " + jwt);
    response.setHeader("Location", envUtil.getEnv("header.auth-redirect-location.value"));
    response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.resetBuffer();
  }
}
