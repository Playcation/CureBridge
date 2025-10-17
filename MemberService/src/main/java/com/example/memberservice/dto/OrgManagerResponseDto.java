package com.example.memberservice.dto;

import com.example.memberservice.entity.OrgManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgManagerResponseDto {

  private Long id;

  private String name;

  private String rank;

  private String number;

  private Long organizationId;

  public static OrgManagerResponseDto toDto(OrgManager orgManager) {
    return OrgManagerResponseDto.builder()
        .id(orgManager.getId())
        .name(orgManager.getName())
        .rank(orgManager.getManagerRank())
        .number(orgManager.getNumber())
        .organizationId(orgManager.getOrganizationId())
        .build();
  }
}
