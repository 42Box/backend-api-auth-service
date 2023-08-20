package com.practice.boxauthservice.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class Oauth42UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    try {
      ClientRegistration clientRegistration = userRequest.getClientRegistration();
      OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
      OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
      Map<String, Object> mutableAttributes = new HashMap<>(oAuth2User.getAttributes());
      Map<String, Object> user_info = signUp(clientRegistration, oAuth2User).orElseThrow(
        RuntimeException::new);
      mutableAttributes.put("user-info", user_info);
      return new DefaultOAuth2User(oAuth2User.getAuthorities(), mutableAttributes,
        clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
    } catch (Exception e) {
      throw new OAuth2AuthenticationException("Registration failed");
    }
  }

  private Optional<Map<String, Object>> signUp(ClientRegistration clientRegistration,
    OAuth2User oAuth2User) {
    String registrationId = clientRegistration.getRegistrationId();
    if (registrationId.equals("42api")) {
      return dummy42Result(oAuth2User);
    }
    throw new OAuth2AuthenticationException("not supported registrationId");
  }

  private Optional<Map<String, Object>> dummy42Result(OAuth2User oAuth2User) {
    // 개발중인 더미 서비스
    // user-service 에 요청을 보내 회원가입을 진행 예정
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("userId", 1);
    attributes.put("userUuid", UUID.randomUUID());
    attributes.put("userNickname", oAuth2User.getName());
    return Optional.of(attributes);
  }
}
