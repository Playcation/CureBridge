package com.example.memberservice.dto;

import java.util.Date;
import lombok.Getter;

@Getter
public class UpdateUserRequestDto {

  private String phoneNumber;

  private Date birth;

  private String password;
}
