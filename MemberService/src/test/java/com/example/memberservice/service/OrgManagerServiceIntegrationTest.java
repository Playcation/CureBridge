package com.example.memberservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.Role;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.OrgManagerRepository;
import com.example.memberservice.repository.UserRepository;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrgManagerServiceIntegrationTest {

  @Autowired
  private OrgManagerService orgManagerService;

  @Autowired
  private OrgManagerRepository orgManagerRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("매니저가 사용자를 조직에 성공적으로 초대한다")
  void inviteUserIntegrationTest() {
    // 1. Given: 매니저 생성 및 사용자 등록
    Long targetOrgId = 500L;
    OrgManagerResponseDto manager = orgManagerService.createOrgManager(
        new OrgManagerCreateRequestDto("매니저A", "팀장", "010-1111-2222", targetOrgId)
    );

    String userEmail = "user@test.com";
    userRepository.save(new User(userEmail, "password", "초대받을 유저", Role.USER, "010-1234-5678",
        new Date(2024, Calendar.NOVEMBER, 10)));

    // 2. When: 초대 실행
    UserInviteDto inviteDto = new UserInviteDto(userEmail, "초대받을유저");
    String result = orgManagerService.inviteUser(manager.getId(), inviteDto);

    // 3. Then: 결과 확인 및 유저의 조직 ID 업데이트 검증
    assertThat(result).isEqualTo("초대되었습니다.");

    User updatedUser = userRepository.findUserByEmail(userEmail).orElseThrow();
    assertThat(updatedUser.getOrganizationId()).isEqualTo(targetOrgId);
  }
}