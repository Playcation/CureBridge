package com.example.chat.config;


import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "MemberService", url = "${member.service.url}")
public interface UserClient {

  @GetMapping("/user")
  UserResponseDto findUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestParam Long id
  );

  @GetMapping("/user/{otherUserId}")
  UserResponseDto findUserById(@PathVariable Long otherUserId);
}
