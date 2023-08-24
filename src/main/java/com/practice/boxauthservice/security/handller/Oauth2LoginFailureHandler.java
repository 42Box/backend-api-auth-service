package com.practice.boxauthservice.security.handller;

import com.practice.boxauthservice.global.env.EnvUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

  private final EnvUtil envUtil;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception) throws IOException, ServletException {
    try {
      String serverCheckedErrorKey = envUtil.getEnv("header.server-checked-error.key");
      String serverCheckedErrorValue = envUtil.getEnv("header.server-checked-error.value");
      response.setStatus(503);
      response.setHeader(serverCheckedErrorKey, serverCheckedErrorValue);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.resetBuffer();
      response.getWriter().write("{\"msg\":\"Service Not Available!\", \"code\": -1}");
    } catch (Exception e) {
      response.setStatus(503);
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.resetBuffer();
      response.getWriter().write("{\"msg\":\"Service Something Wrong!\", \"code\": -1}");
    }
  }
}
