package com.example.memberservice.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.memberservice.entity.Organization;
import com.example.memberservice.enums.OrganizationException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  //TODO: 커스텀 예외로 변경
  default Organization findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(
        () -> new NotFoundException(OrganizationException.NOT_FOUND_ORGANIZATION));
  }
}
