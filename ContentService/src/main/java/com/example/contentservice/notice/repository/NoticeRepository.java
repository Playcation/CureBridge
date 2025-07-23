package com.example.contentservice.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.notice.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
	Page<Notice> findAll(Pageable pageable);

	default Notice findByIdOrElseThrow(Long id) {
		Notice notice = findById(id).orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));
		if (notice.getDeletedAt() != null) {
			throw new NotFoundException(BoardErrorCode.DELETED_BOARD);
		}
		return notice;
	}
}