package com.example.memberservice.service;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.repository.OrganizationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationRepository organizationRepository;

  public OrgResponseDto createOrganization(OrgCreateRequestDto orgCreateRequestDto) {
    return OrgResponseDto.toDto(organizationRepository.save(
        Organization.builder()
            .orgName(orgCreateRequestDto.getOrgName())
            .orgNumber(orgCreateRequestDto.getOrgNumber())
            .account(orgCreateRequestDto.getAccount())
            .orgAddress(orgCreateRequestDto.getOrgAddress())
            .ownerNumber(orgCreateRequestDto.getOwnerNumber())
            .ownerName(orgCreateRequestDto.getOwnerName())
            .password("0000")
            .build()
    ));
  }

  public List<OrgResponseDto> getAllOrganization() {
    List<Organization> organizationList = organizationRepository.findAll();
    return organizationList.stream().map(OrgResponseDto::toDto).toList();
  }

  public OrgResponseDto getOrganization(Long id) {
    return OrgResponseDto.toDto(organizationRepository.findByIdOrElseThrow(id));
  }

  public OrgResponseDto updateOrganization(Long id, OrgUpdateDto orgUpdateDto) {
    Organization organization = organizationRepository.findByIdOrElseThrow(id);
    organization.update(orgUpdateDto);
    Organization updatedOrganization = organizationRepository.save(organization);
    return OrgResponseDto.toDto(updatedOrganization);
  }

  // TODO: 커스텀 예외로 변환
  public String deleteOrganization(Long id) {
    Organization organization = organizationRepository.findByIdOrElseThrow(id);
    organization.delete();
    Organization deletedOrganization = organizationRepository.save(organization);
    if (deletedOrganization.getDeletedAt() == null) {
      return "삭제 실패";
    }
    return "삭제 완료";
  }
}
