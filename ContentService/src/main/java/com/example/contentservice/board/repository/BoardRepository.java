package com.example.contentservice.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	Page<Board> findAllByBoardType(BoardType boardType, Pageable pageable);

	default Board findByIdOrElseThrow(Long id) {
		Board board = findById(id).orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));
		if (board.getDeletedAt() != null) {
			throw new NotFoundException(BoardErrorCode.DELETED_BOARD);
		}
		return board;
	}
}