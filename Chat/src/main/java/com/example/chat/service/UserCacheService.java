package com.example.chat.service;

import com.example.chat.config.UserClient;
import com.example.commonmodule.config.RedisTemplateConfig;
import com.example.memberservice.dto.UserResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

  private final UserClient userClient;

  @Cacheable(value = "userCache", key = "#userId")
  public UserResponseDto getUserByCache(String authorizationHeader, Long userId) {
    return userClient.findUser(authorizationHeader, userId);
  }
}
