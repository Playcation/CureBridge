package com.example.memberservice.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.memberservice.entity.Organization;
import com.example.commonmodule.exceptions.OrganizationException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  default Organization findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(
        () -> new NotFoundException(OrganizationException.NOT_FOUND_ORGANIZATION));
  }

  Optional<Organization> findByEmail(String email);

  default Organization findByEmailOrElseThrow(String email) {
    return findByEmail(email).orElseThrow(
        () -> new NotFoundException(OrganizationException.NOT_FOUND_ORGANIZATION));
  }
}
