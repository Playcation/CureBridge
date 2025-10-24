package com.example.memberservice.controller;

import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.utils.JwtParser;
import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgManagerUpdateDto;
import com.example.memberservice.dto.UserInviteDto;
import com.example.memberservice.entity.Role;
import com.example.memberservice.enums.ManagerException;
import com.example.memberservice.enums.OrganizationException;
import com.example.memberservice.security.TokenSettings;
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
  private final JwtParser jwtParser;

  @PostMapping
  public ResponseEntity<OrgManagerResponseDto> createOrgManager(
      @RequestBody OrgManagerCreateRequestDto orgManagerCreateRequestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrg(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.createOrgManager(orgManagerCreateRequestDto));
  }

  @GetMapping
  public ResponseEntity<List<OrgManagerResponseDto>> getAllOrgManager(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrgOrManager(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.getAllOrgManager());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrgManagerResponseDto> getOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrgOrManager(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.getOrgManager(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrgManagerResponseDto> updateOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody OrgManagerUpdateDto orgManagerUpdateDto
  ) {
    checkOrgOrManager(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.updateOrgManager(id, orgManagerUpdateDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteOrgManager(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrgOrManager(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.deleteOrgManager(id));
  }

  @PostMapping("/invite")
  public ResponseEntity<String> inviteUser(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody UserInviteDto userInviteDto
  ) {
    Long id = jwtParser.findUserByToken(authorizationHeader);
    checkManager(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.inviteUser(id, userInviteDto));
  }

  private void checkManager(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_MANAGER.toString().equals(role)) {
      throw new NoAuthorizedException(ManagerException.NO_AUTHORIZED_MANAGER);
    }
  }

  private void checkOrg(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(OrganizationException.NO_AUTHORIZED_ORGANIZATION);
    }
  }

  private void checkOrgOrManager(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_MANAGER.toString().equals(role) && !Role.ORG_ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(ManagerException.NO_AUTHORIZED_MANAGER);
    }
  }

}
