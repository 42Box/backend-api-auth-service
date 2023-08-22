package com.practice.boxauthservice.global.exception;

import lombok.RequiredArgsConstructor;

/**
 * EnvException.java 환경변수가 존재하지 않을 때 발생하는 전용 예외
 *
 * @author middlefitting
 * @version 1.0.0
 * @see RuntimeException
 * @since 2023-08-22
 */
@RequiredArgsConstructor
public class EnvException extends RuntimeException {

}

