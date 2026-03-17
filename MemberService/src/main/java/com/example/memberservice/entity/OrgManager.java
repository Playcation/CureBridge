package com.example.memberservice.entity;

import com.example.commonmodule.base_entity.BaseEntityDeletedAt;
import com.example.memberservice.dto.OrgManagerUpdateDto;
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
public class OrgManager extends BaseEntityDeletedAt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;

  private String password;

  private String name;

  private String managerRank;

  private String number;

  private Long organizationId;

  @Enumerated(value = EnumType.STRING)
  private Role role = Role.ORG_MANAGER;

  public void update(OrgManagerUpdateDto orgManagerUpdateDto) {
    if (orgManagerUpdateDto.getRank() != null) {
      this.managerRank = orgManagerUpdateDto.getRank();
    }
    if (orgManagerUpdateDto.getNumber() != null) {
      this.number = orgManagerUpdateDto.getNumber();
    }
    if (orgManagerUpdateDto.getOrganizationId() != null) {
      this.organizationId = orgManagerUpdateDto.getOrganizationId();
    }
  }
}
