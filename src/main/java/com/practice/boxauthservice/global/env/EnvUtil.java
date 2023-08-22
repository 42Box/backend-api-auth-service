package com.practice.boxauthservice.global.env;

import com.practice.boxauthservice.global.exception.EnvException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * EnvUtil.java 환경변수를 가져오는 유틸리티 클래스
 *
 * @author middlefitting
 * @version 1.0.0
 * @see Environment
 * @since 2023-08-22
 */
@Component
@RequiredArgsConstructor
public class EnvUtil {

  private final Environment env;

  public String getEnv(String key) {
    Optional<String> valueOpt = Optional.ofNullable(env.getProperty(key));
    return valueOpt.orElseThrow(EnvException::new);
  }
}
