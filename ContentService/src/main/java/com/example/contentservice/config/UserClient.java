package com.example.contentservice.config;

import com.example.commonmodule.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userClient", url = "${member.service.url}")
public interface UserClient {

  @GetMapping("/user/{userId}")
  UserResponseDto getUserInfoById(@PathVariable("userId") Long userId);
}