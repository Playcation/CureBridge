package com.example.memberservice.service;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgManagerUpdateDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.OrgManager;
import com.example.memberservice.entity.User;
import com.example.memberservice.repository.OrgManagerRepository;
import com.example.memberservice.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgManagerService {

  private final OrgManagerRepository orgManagerRepository;
  private final UserRepository userRepository;

  public OrgManagerResponseDto createOrgManager(
      OrgManagerCreateRequestDto orgManagerCreateRequestDto) {
    return OrgManagerResponseDto.toDto(orgManagerRepository.save(OrgManager.builder()
        .name(orgManagerCreateRequestDto.getName())
        .managerRank(orgManagerCreateRequestDto.getRank())
        .number(orgManagerCreateRequestDto.getNumber())
        .organizationId(orgManagerCreateRequestDto.getOrganizationId())
        .build()));
  }

  public List<OrgManagerResponseDto> getAllOrgManager() {
    List<OrgManager> orgManagerList = orgManagerRepository.findAll();
    return orgManagerList.stream().map(OrgManagerResponseDto::toDto).toList();
  }

  public OrgManagerResponseDto getOrgManager(Long id) {
    return OrgManagerResponseDto.toDto(orgManagerRepository.findByIdOrElseThrow(id));
  }

  public OrgManagerResponseDto updateOrgManager(Long id, OrgManagerUpdateDto orgManagerUpdateDto) {
    OrgManager orgManager = orgManagerRepository.findByIdOrElseThrow(id);
    orgManager.update(orgManagerUpdateDto);
    return OrgManagerResponseDto.toDto(orgManagerRepository.save(orgManager));
  }

  public String deleteOrgManager(Long id) {
    OrgManager orgManager = orgManagerRepository.findByIdOrElseThrow(id);
    orgManager.delete();
    orgManagerRepository.save(orgManager);
    return "삭제되었습니다.";
  }

  public String inviteUser(Long id, UserInviteDto userInviteDto) {
    OrgManager orgManager = orgManagerRepository.findByIdOrElseThrow(id);
    User user = userRepository.findUserByEmail(userInviteDto.getEmail())
        .orElseThrow(() -> new RuntimeException("aaa"));
    user.registrationOrg(orgManager.getOrganizationId());
    userRepository.save(user);
    return "초대되었습니다.";
  }
}
