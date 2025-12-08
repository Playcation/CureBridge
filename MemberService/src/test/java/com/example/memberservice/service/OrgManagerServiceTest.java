package com.example.memberservice.service;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgManagerUpdateDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.OrgManager;
import com.example.memberservice.entity.Role;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.OrgManagerRepository;
import com.example.memberservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class OrgManagerServiceTest {

	private OrgManagerRepository orgManagerRepository;
	private UserRepository userRepository;
	private OrgManagerService orgManagerService;

	@BeforeEach
	void setUp() {
		orgManagerRepository = mock(OrgManagerRepository.class);
		userRepository = mock(UserRepository.class);
		orgManagerService = new OrgManagerService(orgManagerRepository, userRepository);
	}

	@Test
	@DisplayName("조직 관리자 생성 성공")
	void createOrgManager_success() {
		OrgManagerCreateRequestDto dto = new OrgManagerCreateRequestDto("홍길동", "팀장", "01012345678", 1L);

		OrgManager savedManager = OrgManager.builder()
			.id(1L)
			.name(dto.getName())
			.managerRank(dto.getRank())
			.number(dto.getNumber())
			.organizationId(dto.getOrganizationId())
			.build();

		when(orgManagerRepository.save(any(OrgManager.class))).thenReturn(savedManager);

		OrgManagerResponseDto response = orgManagerService.createOrgManager(dto);

		verify(orgManagerRepository, times(1)).save(any(OrgManager.class));
		assertThat(response.getName()).isEqualTo("홍길동");
		assertThat(response.getRank()).isEqualTo("팀장");
	}

	@Test
	@DisplayName("전체 조직 관리자 조회")
	void getAllOrgManager_success() {
		OrgManager manager1 = OrgManager.builder().id(1L).name("관리자1").build();
		OrgManager manager2 = OrgManager.builder().id(2L).name("관리자2").build();

		when(orgManagerRepository.findAll()).thenReturn(List.of(manager1, manager2));

		List<OrgManagerResponseDto> result = orgManagerService.getAllOrgManager();

		verify(orgManagerRepository, times(1)).findAll();
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("관리자1");
	}

	@Test
	@DisplayName("특정 조직 관리자 조회")
	void getOrgManager_success() {
		OrgManager manager = OrgManager.builder().id(1L).name("조회관리자").build();
		when(orgManagerRepository.findByIdOrElseThrow(1L)).thenReturn(manager);

		OrgManagerResponseDto response = orgManagerService.getOrgManager(1L);

		verify(orgManagerRepository, times(1)).findByIdOrElseThrow(1L);
		assertThat(response.getName()).isEqualTo("조회관리자");
	}

	@Test
	@DisplayName("조직 관리자 수정 성공")
	void updateOrgManager_success() {
		OrgManager manager = OrgManager.builder().id(1L).name("기존관리자").build();
		OrgManagerUpdateDto updateDto = new OrgManagerUpdateDto("부장", "01099998888", 1L);

		when(orgManagerRepository.findByIdOrElseThrow(1L)).thenReturn(manager);
		when(orgManagerRepository.save(any(OrgManager.class))).thenAnswer(invocation -> invocation.getArgument(0));

		OrgManagerResponseDto response = orgManagerService.updateOrgManager(1L, updateDto);

		verify(orgManagerRepository, times(1)).save(manager);
		assertThat(response.getRank()).isEqualTo("부장");
		assertThat(response.getNumber()).isEqualTo("01099998888");
	}

	@Test
	@DisplayName("조직 관리자 삭제 성공")
	void deleteOrgManager_success() {
		OrgManager manager = OrgManager.builder().id(1L).name("삭제관리자").build();
		when(orgManagerRepository.findByIdOrElseThrow(1L)).thenReturn(manager);
		when(orgManagerRepository.save(manager)).thenReturn(manager);

		String result = orgManagerService.deleteOrgManager(1L);

		verify(orgManagerRepository, times(1)).save(manager);
		assertThat(result).isEqualTo("삭제되었습니다.");
	}

	@Test
	@DisplayName("유저 초대 성공")
	void inviteUser_success() {
		OrgManager manager = OrgManager.builder().id(1L).organizationId(100L).name("관리자").build();
		// User 생성자: (email, password, name, role, phoneNumber, birth)
		User user = new User(
			"test@test.com",
			"encodedPw",
			"홍길동",
			Role.USER,
			"01012345678",
			LocalDate.of(1990, 1, 1)
		);


		when(orgManagerRepository.findByIdOrElseThrow(1L)).thenReturn(manager);
		when(userRepository.findUserByEmail("test@test.com")).thenReturn(Optional.of(user));
		when(userRepository.save(user)).thenReturn(user);

		UserInviteDto inviteDto = new UserInviteDto("test@test.com", "홍길동");
		String result = orgManagerService.inviteUser(1L, inviteDto);

		verify(userRepository, times(1)).save(user);
		assertThat(result).isEqualTo("초대되었습니다.");
	}

	@Test
	@DisplayName("유저 초대 실패 - 이메일 없음")
	void inviteUser_fail_noUser() {
		OrgManager manager = OrgManager.builder().organizationId(100L).name("관리자").build();

		when(orgManagerRepository.findByIdOrElseThrow(1L)).thenReturn(manager);
		when(userRepository.findUserByEmail("notfound@test.com")).thenReturn(Optional.empty());

		UserInviteDto inviteDto = new UserInviteDto("notfound@test.com", "관리자");

		assertThatThrownBy(() -> orgManagerService.inviteUser(1L, inviteDto))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("aaa");
	}
}
