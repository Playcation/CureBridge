package com.example.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrgCreateRequestDto {

  private String account;
  
  private String orgName;

  private String orgNumber;

  private String ownerName;

  private String ownerNumber;

  private String orgAddress;
}
