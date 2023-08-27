package com.practice.boxauthservice.config;

/**
 * TomcatConfig.
 *
 * @author : middlefitting
 * @since : 2023/08/28
 */

import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;

@Configuration
public class TomcatConfig {

  @Bean
  public TomcatContextCustomizer sameSiteCookiesConfig() {
    return context -> {
      final Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
      cookieProcessor.setSameSiteCookies("None");
      context.setCookieProcessor(cookieProcessor);
    };
  }
}
