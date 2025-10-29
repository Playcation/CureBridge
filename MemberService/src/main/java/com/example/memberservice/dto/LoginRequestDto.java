package com.example.memberservice.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {

  private String email;
  private String password;
  private String role;
}
