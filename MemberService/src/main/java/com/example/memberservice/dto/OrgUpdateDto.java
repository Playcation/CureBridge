package com.example.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrgUpdateDto {

  private String password;

  private String orgNumber;

  private String ownerName;

  private String ownerNumber;

  private String orgAddress;

}
