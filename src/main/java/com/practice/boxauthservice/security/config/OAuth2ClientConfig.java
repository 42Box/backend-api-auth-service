package com.practice.boxauthservice.security.config;

import com.practice.boxauthservice.security.handller.Oauth2LoginFailureHandler;
import com.practice.boxauthservice.security.handller.Oauth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
public class OAuth2ClientConfig {

  private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;
  private final Oauth2LoginFailureHandler oauth2LoginFailureHandler;

  @Autowired
  public OAuth2ClientConfig(Oauth2LoginSuccessHandler oauth2LoginSuccessHandler,
      Oauth2LoginFailureHandler oauth2LoginFailureHandler) {
    this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
    this.oauth2LoginFailureHandler = oauth2LoginFailureHandler;
  }

  @Bean
  SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeRequests().anyRequest().authenticated();
    http.oauth2Login().successHandler(oauth2LoginSuccessHandler);
    http.oauth2Login().failureHandler(oauth2LoginFailureHandler);
    http.formLogin().disable().httpBasic().disable().csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.cors().configurationSource(corsConfigurationSource());
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.addAllowedOrigin("https://www.42box.kr");
    configuration.addAllowedOrigin("*");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
//    configuration.setAllowCredentials(true);
    configuration.setMaxAge(1L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
