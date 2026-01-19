package com.example.memberservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.verify;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.OrgManager;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.OrgManagerRepository;
import com.example.memberservice.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrgManagerServiceTest {

  @Mock
  private OrgManagerRepository orgManagerRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private OrgManagerService orgManagerService;

  @Test
  @DisplayName("매니저 생성 테스트")
  void createOrgManager_Success() {
    // given
    OrgManagerCreateRequestDto requestDto = new OrgManagerCreateRequestDto("Manager", "Rank",
        "010-1111-2222", 100L);
    OrgManager orgManager = OrgManager.builder().id(1L).name("Manager").build();
    given(orgManagerRepository.save(any(OrgManager.class))).willReturn(orgManager);

    // when
    OrgManagerResponseDto response = orgManagerService.createOrgManager(requestDto);

    // then
    assertEquals("Manager", response.getName());
    verify(orgManagerRepository).save(any(OrgManager.class));
  }

  @Test
  @DisplayName("사용자 초대 테스트 - 성공")
  void inviteUser_Success() {
    // given
    Long managerId = 1L;
    Long organizationId = 100L;
    UserInviteDto inviteDto = new UserInviteDto("wrong@example.com", "userName");

    OrgManager manager = OrgManager.builder().id(managerId).organizationId(organizationId).build();
    User user = spy(new User()); // registrationOrg 동작 확인을 위해 spy 사용 가능

    given(orgManagerRepository.findByIdOrElseThrow(managerId)).willReturn(manager);
    given(userRepository.findUserByEmail(inviteDto.getEmail())).willReturn(Optional.of(user));

    // when
    String result = orgManagerService.inviteUser(managerId, inviteDto);

    // then
    assertEquals("초대되었습니다.", result);
    verify(user).registrationOrg(organizationId); // 유저에게 조직 ID가 할당되었는지 확인
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("사용자 초대 테스트 - 사용자를 찾을 수 없는 경우")
  void inviteUser_UserNotFound_ThrowsException() {
    // given
    Long managerId = 1L;
    UserInviteDto inviteDto = new UserInviteDto("wrong@example.com", "userName");

    given(orgManagerRepository.findByIdOrElseThrow(managerId)).willReturn(
        OrgManager.builder().build());
    given(userRepository.findUserByEmail(anyString())).willReturn(Optional.empty());

    // when & then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      orgManagerService.inviteUser(managerId, inviteDto);
    });
    assertEquals("aaa", exception.getMessage());
  }
}