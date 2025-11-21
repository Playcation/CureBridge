package com.example.memberservice.controller;

import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgManagerUpdateDto;
import com.example.commonmodule.config.TokenSettings;
import com.example.memberservice.service.OrgManagerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

  private final OrgManagerService orgManagerService;

  @PostMapping
  public ResponseEntity<OrgManagerResponseDto> createOrgManager(
      @RequestBody OrgManagerCreateRequestDto orgManagerCreateRequestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    return ResponseEntity.ok().body(orgManagerService.createOrgManager(orgManagerCreateRequestDto));
  }

  @GetMapping
  public ResponseEntity<List<OrgManagerResponseDto>> getAllOrgManager(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    return ResponseEntity.ok().body(orgManagerService.getAllOrgManager());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrgManagerResponseDto> getOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    return ResponseEntity.ok().body(orgManagerService.getOrgManager(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrgManagerResponseDto> updateOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody OrgManagerUpdateDto orgManagerUpdateDto
  ) {
    return ResponseEntity.ok().body(orgManagerService.updateOrgManager(id, orgManagerUpdateDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    return ResponseEntity.ok().body(orgManagerService.deleteOrgManager(id));
  }

  @PostMapping("/invite")
  public ResponseEntity<OrgManagerResponseDto> inviteUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    return null;
  }

}
