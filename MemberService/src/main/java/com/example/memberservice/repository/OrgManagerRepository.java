package com.example.memberservice.repository;

import com.example.memberservice.entity.OrgManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgManagerRepository extends JpaRepository<OrgManager, Long> {

  default OrgManager findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(() -> new RuntimeException("삭제된 유저입니다."));
  }
}
