package com.example.memberservice.repository;

import com.example.memberservice.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  //TODO: 커스텀 예외로 변경
  default Organization findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new RuntimeException("삭제된 유저입니다."));
  }
}
