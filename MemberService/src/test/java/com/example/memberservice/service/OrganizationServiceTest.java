package com.example.memberservice.service;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrganizationServiceTest {

	private OrganizationRepository organizationRepository;
	private OrganizationService organizationService;

	@BeforeEach
	void setUp() {
		organizationRepository = mock(OrganizationRepository.class);
		organizationService = new OrganizationService(organizationRepository);
	}

	@Test
	@DisplayName("조직 생성 성공")
	void createOrganization_success() {
		OrgCreateRequestDto dto = new OrgCreateRequestDto(
			"test@test.com", "테스트조직", "0212345678",
			"홍길동", "01012345678", "서울시 강남구"
		);

		Organization savedOrg = Organization.builder()
			.orgName(dto.getOrgName())
			.orgNumber(dto.getOrgNumber())
			.email(dto.getAccount())
			.orgAddress(dto.getOrgAddress())
			.ownerNumber(dto.getOwnerNumber())
			.ownerName(dto.getOwnerName())
			.password("0000")
			.build();

		when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrg);

		OrgResponseDto response = organizationService.createOrganization(dto);

		verify(organizationRepository, times(1)).save(any(Organization.class));
		assertThat(response.getOrgName()).isEqualTo("테스트조직");
		assertThat(response.getOwnerName()).isEqualTo("홍길동");
	}

	@Test
	@DisplayName("전체 조직 조회")
	void getAllOrganization_success() {
		Organization org1 = Organization.builder().id(1L).orgName("조직1").build();
		Organization org2 = Organization.builder().id(2L).orgName("조직2").build();

		when(organizationRepository.findAll()).thenReturn(List.of(org1, org2));

		List<OrgResponseDto> result = organizationService.getAllOrganization();

		verify(organizationRepository, times(1)).findAll();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getOrgName()).isEqualTo("조직1");
	}

	@Test
	@DisplayName("특정 조직 조회")
	void getOrganization_success() {
		Organization org = Organization.builder().id(1L).orgName("조회조직").build();
		when(organizationRepository.findByIdOrElseThrow(1L)).thenReturn(org);

		OrgResponseDto response = organizationService.getOrganization(1L);

		verify(organizationRepository, times(1)).findByIdOrElseThrow(1L);
		assertThat(response.getOrgName()).isEqualTo("조회조직");
	}

	@Test
	@DisplayName("조직 수정 성공")
	void updateOrganization_success() {
		// 기존 조직 엔티티
		Organization org = Organization.builder()
			.orgName("기존조직")
			.orgNumber("11111")
			.ownerName("홍길동")
			.ownerNumber("01011112222")
			.orgAddress("서울시 강남구")
			.password("0000")
			.build();

		// 모든 필드를 채운 OrgUpdateDto
		OrgUpdateDto updateDto = new OrgUpdateDto(
			"9999",
			"22222",
			"김철수",
			"01099998888",
			"서울시 서초구"
		);

		when(organizationRepository.findByIdOrElseThrow(1L)).thenReturn(org);
		when(organizationRepository.save(any(Organization.class)))
			.thenAnswer(invocation -> invocation.getArgument(0));

		// 실행
		OrgResponseDto response = organizationService.updateOrganization(1L, updateDto);

		// 검증
		verify(organizationRepository, times(1)).save(org);
		assertThat(response.getOrgNumber()).isEqualTo("22222");
		assertThat(response.getOwnerName()).isEqualTo("김철수");
		assertThat(response.getOwnerNumber()).isEqualTo("01099998888");
		assertThat(response.getOrgAddress()).isEqualTo("서울시 서초구");
	}


	@Test
	@DisplayName("조직 삭제 성공")
	void deleteOrganization_success() {
		Organization org = Organization.builder().id(1L).orgName("삭제조직").build();

		when(organizationRepository.findByIdOrElseThrow(1L)).thenReturn(org);
		when(organizationRepository.save(org)).thenReturn(org);

		String result = organizationService.deleteOrganization(1L);

		verify(organizationRepository, times(1)).save(org);
		assertThat(result).isEqualTo("삭제 완료");
	}

	// 삭제 실패가 발생하지 않음?
	// 항상 org.delete() 호출하면 항상 deletedAt을 설정하므로 실패 발생 X
	// 해당 부분 조건부로 설정하도록 하면 좋을듯함.
	//	ex) 이미 deletedAt이 존재하는 경우 throw
}
