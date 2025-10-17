package com.example.memberservice.controller;

import com.example.memberservice.dto.OrgCreateRequestDto;
import com.example.memberservice.dto.OrgResponseDto;
import com.example.memberservice.dto.OrgUpdateDto;
import com.example.memberservice.security.JwtUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

  private final OrganizationService organizationService;
  private final JwtUtil jwtUtil;

  @PostMapping("/create")
  public ResponseEntity<OrgResponseDto> createOrganization(
      @RequestBody OrgCreateRequestDto orgCreateRequestDto
  ) {
    return ResponseEntity.ok().body(organizationService.createOrganization(orgCreateRequestDto));
  }

  @GetMapping
  public ResponseEntity<List<OrgResponseDto>> getAllOrganization() {
    return ResponseEntity.ok().body(organizationService.getAllOrganization());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrgResponseDto> getOrganization(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok().body(organizationService.getOrganization(id));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<OrgResponseDto> updateOrganization(
      @PathVariable Long id,
      @RequestBody OrgUpdateDto orgUpdateDto
  ) {
    return ResponseEntity.ok().body(organizationService.updateOrganization(id, orgUpdateDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteOrganization(
      @PathVariable Long id
  ) {
    return ResponseEntity.ok().body(organizationService.deleteOrganization(id));
  }

  @PostMapping("/create/employee")
  public String createEmployee() {
    return "hello";
  }

}
