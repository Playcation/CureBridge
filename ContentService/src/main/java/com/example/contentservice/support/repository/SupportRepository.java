package com.example.contentservice.support.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.support.entity.Support;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
	default Support findByIdOrElseThrow(Long id) {
		Support support = findById(id).orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));

		return support;
	}
}
