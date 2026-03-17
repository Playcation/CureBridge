package com.example.memberservice.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.memberservice.entity.OrgManager;
import com.example.commonmodule.exceptions.ManagerException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgManagerRepository extends JpaRepository<OrgManager, Long> {

  default OrgManager findByIdOrElseThrow(Long id) {
    return findById(id).orElseThrow(
        () -> new NotFoundException(ManagerException.NOT_FOUND_MANAGER));
  }

  Optional<OrgManager> findByEmail(String email);

  default OrgManager findByEmailOrElseThrow(String email) {
    return findByEmail(email).orElseThrow(
        () -> new NotFoundException(ManagerException.NOT_FOUND_MANAGER));
  }
}
