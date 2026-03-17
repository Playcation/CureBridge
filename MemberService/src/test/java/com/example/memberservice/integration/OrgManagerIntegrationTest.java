package com.example.memberservice.integration;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgManagerUpdateDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.OrgManager;
import com.example.commonmodule.utils.Role;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.OrgManagerRepository;
import com.example.memberservice.repository.UserRepository;
import com.example.memberservice.service.OrgManagerService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrgManagerIntegrationTest {

	@Autowired
	private OrgManagerService orgManagerService;

	@Autowired
	private OrgManagerRepository orgManagerRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("조직 관리자 생성 후 유저 초대")
	void createManagerAndInviteUser() {
		// 1. 조직 관리자 생성
		OrgManagerCreateRequestDto managerDto = new OrgManagerCreateRequestDto(
			"관리자홍", "팀장", "01011112222", 1L
		);
		OrgManagerResponseDto managerResponse = orgManagerService.createOrgManager(managerDto);

		assertThat(managerResponse.getName()).isEqualTo("관리자홍");

		// 2. 유저 생성
		User user = new User(
			"invite@test.com",
			"pw",
			"초대유저",
			Role.USER,
			"01099998888",
			LocalDate.of(1995, 5, 5)
		);
		userRepository.save(user);

		// 3. 유저 초대
		UserInviteDto inviteDto = new UserInviteDto("invite@test.com", "초대유저");
		String result = orgManagerService.inviteUser(managerResponse.getId(), inviteDto);

		assertThat(result).isEqualTo("초대되었습니다.");
		assertThat(userRepository.findUserByEmail("invite@test.com").get().getOrganizationId())
			.isEqualTo(managerResponse.getOrganizationId());
	}

	@Test
	@DisplayName("조직 관리자 수정")
	void updateOrgManagerIntegrationTest() {
		// 1. 생성
		OrgManagerCreateRequestDto dto = new OrgManagerCreateRequestDto( "관리자김", "대리", "01022223333", 2L );
		OrgManagerResponseDto created = orgManagerService.createOrgManager(dto);

		// 2. 수정
		OrgManagerUpdateDto updateDto = new OrgManagerUpdateDto( "부장", "01099998888", 2L );
		OrgManagerResponseDto updated = orgManagerService.updateOrgManager(created.getId(), updateDto);

		// 3. 검증
		assertThat(updated.getRank()).isEqualTo("부장");
		assertThat(updated.getNumber()).isEqualTo("01099998888");
	}

	@Test
	@DisplayName("조직 관리자 삭제")
	void deleteOrgManagerIntegrationTest() {
		// 1. 생성
		OrgManagerCreateRequestDto dto = new OrgManagerCreateRequestDto("삭제관리자", "사원", "01033334444", 3L);
		OrgManagerResponseDto created = orgManagerService.createOrgManager(dto);

		// 2. 삭제
		String result = orgManagerService.deleteOrgManager(created.getId());

		// 3. 검증
		assertThat(result).isEqualTo("삭제되었습니다.");
		OrgManager deleted = orgManagerRepository.findByIdOrElseThrow(created.getId());
		assertThat(deleted.getDeletedAt()).isNotNull();
	}
}

