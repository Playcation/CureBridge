package com.example.memberservice.controller;

import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.utils.JwtParser;
import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgManagerCreateRequestDto;
import com.example.memberservice.dto.OrgManagerResponseDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.entity.Role;
import com.example.memberservice.enums.AdminException;
import com.example.memberservice.enums.ManagerException;

import com.example.memberservice.service.OrgManagerService;
import com.example.memberservice.service.OrganizationService;
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
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

  private final OrganizationService organizationService;
  private final OrgManagerService orgManagerService;
  private final JwtParser jwtParser;

  @PostMapping("/create")
  public ResponseEntity<OrgResponseDto> createOrganization(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody OrgCreateRequestDto orgCreateRequestDto
  ) {
    checkAdmin(authorizationHeader);
    return ResponseEntity.ok().body(organizationService.createOrganization(orgCreateRequestDto));
  }

  @GetMapping
  public ResponseEntity<List<OrgResponseDto>> getAllOrganization(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrgOrAdmin(authorizationHeader);
    return ResponseEntity.ok().body(organizationService.getAllOrganization());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrgResponseDto> getOrganization(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    checkOrgOrAdmin(authorizationHeader);
    return ResponseEntity.ok().body(organizationService.getOrganization(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrgResponseDto> updateOrganization(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long id,
      @RequestBody OrgUpdateDto orgUpdateDto
  ) {
    checkOrgOrAdmin(authorizationHeader);
    return ResponseEntity.ok().body(organizationService.updateOrganization(id, orgUpdateDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteOrganization(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok().body(organizationService.deleteOrganization(id));
  }

  @PostMapping("/create/employee")
  public ResponseEntity<OrgManagerResponseDto> createEmployee(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestBody OrgManagerCreateRequestDto orgManagerCreateRequestDto
  ) {
    checkOrg(authorizationHeader);
    return ResponseEntity.ok().body(orgManagerService.createOrgManager(orgManagerCreateRequestDto));
  }

  private void checkAdmin(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_MANAGER.toString().equals(role)) {
      throw new NoAuthorizedException(ManagerException.NO_AUTHORIZED_MANAGER);
    }
  }

  private void checkOrg(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(AdminException.NO_AUTHORIZED_ADMIN);
    }
  }

  private void checkOrgOrAdmin(String authorizationHeader) {
    String role = jwtParser.parseRole(authorizationHeader);
    if (!Role.ORG_MANAGER.toString().equals(role) && !Role.ORG_ADMIN.toString().equals(role)) {
      throw new NoAuthorizedException(ManagerException.NO_AUTHORIZED_MANAGER);
    }
  }

}
