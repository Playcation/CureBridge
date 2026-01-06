package com.example.memberservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.doAnswer;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.repository.OrganizationRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

  @Mock
  private OrganizationRepository organizationRepository;

  @InjectMocks
  private OrganizationService organizationService;

  @Test
  @DisplayName("조직 생성 테스트")
  void createOrganization_Success() {
    // given
    OrgCreateRequestDto requestDto = new OrgCreateRequestDto(
        "OrgName", "123-45-67890", "test@org.com", "Address", "010-1234-5678", "Owner"
    );
    Organization org = Organization.builder().id(1L).orgName("OrgName").build();
    given(organizationRepository.save(any(Organization.class))).willReturn(org);

    // when
    OrgResponseDto response = organizationService.createOrganization(requestDto);

    // then
    assertNotNull(response);
    verify(organizationRepository, times(1)).save(any(Organization.class));
  }

  @Test
  @DisplayName("조직 수정 테스트")
  void updateOrganization_Success() {
    // given
    Long orgId = 1L;
    OrgUpdateDto updateDto = new OrgUpdateDto("password", "010-0000-0000", "New Name",
        "010-1234-5678", "New Address");
    Organization organization = Organization.builder().id(orgId).orgName("Old Name").build();

    given(organizationRepository.findByIdOrElseThrow(orgId)).willReturn(organization);
    given(organizationRepository.save(any(Organization.class))).willReturn(organization);

    // when
    OrgResponseDto response = organizationService.updateOrganization(orgId, updateDto);

    // then
    assertNotNull(response);
    verify(organizationRepository).save(organization);
  }

  @Test
  @DisplayName("조직 삭제 테스트 - 성공")
  void deleteOrganization_Success() {
    // given
    Long orgId = 1L;
    Organization organization = spy(Organization.builder().id(orgId).build());

    given(organizationRepository.findByIdOrElseThrow(orgId)).willReturn(organization);
    // delete() 호출 시 deletedAt이 설정되었다고 가정 (BaseEntity 동작 시뮬레이션)
    doAnswer(invocation -> {
      organization.delete(); // 실제 delete() 메서드 호출
      return organization;
    }).when(organizationRepository).save(organization);

    // Mockito의 spy나 실제 필드 주입 없이 deletedAt이 null이 아니라고 가정하기 위해 상황 설정
    given(organization.getDeletedAt()).willReturn(LocalDateTime.now());

    // when
    String result = organizationService.deleteOrganization(orgId);

    // then
    assertEquals("삭제 완료", result);
  }
}