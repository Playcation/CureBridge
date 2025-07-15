package com.example.contentservice.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contentservice.board.entity.Board;
import com.example.contentservice.board.entity.BoardType;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	List<Board> findAllByBoardType(BoardType boardType);
}