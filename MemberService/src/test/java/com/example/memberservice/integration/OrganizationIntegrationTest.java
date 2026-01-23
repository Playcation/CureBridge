package com.example.memberservice.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.repository.OrganizationRepository;
import com.example.memberservice.service.OrganizationService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrganizationIntegrationTest {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Test
	@DisplayName("조직 생성 후 조회까지 통합 테스트")
	void createAndGetOrganization() {
		OrgCreateRequestDto dto = new OrgCreateRequestDto(
			"test@test.com", "테스트조직", "11111",
			"홍길동", "01012345678", "서울시 강남구"
		);

		OrgResponseDto created = organizationService.createOrganization(dto);
		OrgResponseDto found = organizationService.getOrganization(created.getId());

		assertThat(found.getOrgName()).isEqualTo("테스트조직");
		assertThat(found.getOwnerName()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("조직 수정 통합 테스트")
	void updateOrganizationIntegrationTest() {
		// 1. 조직 생성
		OrgCreateRequestDto dto = new OrgCreateRequestDto(
			"test@test.com", "테스트조직", "11111",
			"홍길동", "01012345678", "서울시 강남구"
		);
		OrgResponseDto created = organizationService.createOrganization(dto);

		// 2. 조직 수정
		OrgUpdateDto updateDto = new OrgUpdateDto(
			"54321", "01077778888",
			"김철수", "01099998888", "서울시 서초구"
		);
		OrgResponseDto updated = organizationService.updateOrganization(created.getId(), updateDto);

		// 3. 검증
		assertThat(updated.getOwnerName()).isEqualTo("김철수");
		assertThat(updated.getOrgAddress()).isEqualTo("서울시 서초구");
	}

	@Test
	@DisplayName("조직 삭제 통합 테스트")
	void deleteOrganizationIntegrationTest() {
		// 1. 조직 생성
		OrgCreateRequestDto dto = new OrgCreateRequestDto(
			"delete@test.com", "삭제조직", "11111",
			"박영희", "01022223333", "서울시 송파구"
		);
		OrgResponseDto created = organizationService.createOrganization(dto);

		// 2. 삭제
		String result = organizationService.deleteOrganization(created.getId());

		// 3. 검증
		assertThat(result).isEqualTo("삭제 완료");
		Organization deleted = organizationRepository.findByIdOrElseThrow(created.getId());
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}
