package com.example.memberservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.repository.OrganizationRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test") // application-test.properties 적용
@Transactional // 테스트 후 자동 롤백
class OrganizationServiceIntegrationTest {

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private OrganizationRepository organizationRepository;

  @BeforeEach
  void setUp() {
    organizationRepository.deleteAll();
  }

  @Test
  @DisplayName("조직 생성 및 조회 통합 테스트")
  void createAndGetOrganizationTest() {
    // given
    OrgCreateRequestDto request = new OrgCreateRequestDto(
        "테스트조직", "123-45-67890", "test@org.com", "서울시", "010-1234-5678", "대표자"
    );

    // when
    OrgResponseDto savedOrg = organizationService.createOrganization(request);
    OrgResponseDto foundOrg = organizationService.getOrganization(savedOrg.getId());

    // then
    assertThat(foundOrg.getOrgName()).isEqualTo("테스트조직");
    assertThat(foundOrg.getAccount()).isEqualTo("test@org.com");
  }

  @Test
  @DisplayName("조직 정보 수정 테스트")
  void updateOrganizationTest() {
    // given
    OrgResponseDto org = organizationService.createOrganization(
        new OrgCreateRequestDto("기존조직", "111", "old@test.com", "주소", "010", "이름")
    );
    OrgUpdateDto updateDto = new OrgUpdateDto("newPass", "010-9999-9999", "새대표", "010-8888-8888",
        "새주소");

    // when
    OrgResponseDto updated = organizationService.updateOrganization(org.getId(), updateDto);

    // then
    assertThat(updated.getOrgAddress()).isEqualTo("새주소");
    assertThat(updated.getOwnerName()).isEqualTo("새대표");
  }

  @Test
  @DisplayName("조직 삭제(소프트 딜리트) 검증")
  void deleteOrganizationTest() {
    // given
    OrgResponseDto org = organizationService.createOrganization(
        new OrgCreateRequestDto("삭제조직", "222", "del@test.com", "주소", "010", "이름")
    );

    // when
    String result = organizationService.deleteOrganization(org.getId());

    // then
    assertThat(result).isEqualTo("삭제 완료");
    // findAll은 기본적으로 deletedAt이 null인 것만 가져오는지 확인 필요 (BaseEntity 설정에 따라 다름)
    List<OrgResponseDto> all = organizationService.getAllOrganization();
    assertThat(all).isEmpty();
  }
}