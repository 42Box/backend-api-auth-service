package com.practice.boxauthservice.security.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PostUsersResponseDto.
 *
 * @author : middlefitting
 * @since : 2023/08/25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostUsersResponseDto {

  String role;
  String uuid;
  String profileImagePath;
  String profileImageUrl;
}
