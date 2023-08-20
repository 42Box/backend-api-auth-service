package com.practice.boxauthservice.global.env;

import com.practice.boxauthservice.global.exception.EnvException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvUtil {

  private final Environment env;

  public String getEnv(String key) {
    Optional<String> valueOpt = Optional.ofNullable(env.getProperty(key));
    return valueOpt.orElseThrow(EnvException::new);
  }
}
