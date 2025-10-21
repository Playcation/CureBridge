package com.example.memberservice.dto;

import com.example.memberservice.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgResponseDto {

  private Long id;

  private String account;

  private String orgName;

  private String orgNumber;

  private String ownerName;

  private String ownerNumber;

  private String orgAddress;

  public static OrgResponseDto toDto(Organization org) {
    return OrgResponseDto.builder()
        .id(org.getId())
        .account(org.getAccount())
        .orgName(org.getOrgName())
        .orgNumber(org.getOrgNumber())
        .ownerName(org.getOwnerName())
        .ownerNumber(org.getOwnerNumber())
        .orgAddress(org.getOrgAddress())
        .build();
  }
}
