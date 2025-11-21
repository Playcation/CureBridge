package com.example.chat.service;

import com.example.chat.config.UserClient;

import com.example.commonmodule.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCacheService {

  private final UserClient userClient;

  @Cacheable(value = "userCache", key = "#userId", unless="#result == null")
  public UserResponseDto getUserByCache(Long userId) {
    return userClient.findUserById(userId);
  }
}
