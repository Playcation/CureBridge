package com.example.memberservice.entity;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.commonmodule.utils.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntityDeletedAt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String password;

  private String orgName;

  private String orgNumber;

  private String ownerName;

  private String ownerNumber;

  private String orgAddress;

  @Enumerated(value = EnumType.STRING)
  private Role role = Role.ORG_ADMIN;

  public void update(OrgUpdateDto orgUpdateDto) {
    if (orgUpdateDto.getOrgAddress() != null) {
      this.orgAddress = orgUpdateDto.getOrgAddress();
    }
    if (orgUpdateDto.getOrgNumber() != null) {
      this.orgNumber = orgUpdateDto.getOrgNumber();
    }
    if (orgUpdateDto.getOwnerName() != null) {
      this.ownerName = orgUpdateDto.getOwnerName();
    }
    if (orgUpdateDto.getOwnerNumber() != null) {
      this.ownerNumber = orgUpdateDto.getOwnerNumber();
    }
  }

}
