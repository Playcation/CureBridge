package com.example.commonmodule.files.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.commonmodule.files.entity.BoardFile;
import com.example.commonmodule.files.entity.BoardFileType;

@Repository
public interface BoardFileRepository extends JpaRepository<BoardFile, Long> {

	List<BoardFile> findByBoardId(Long boardId);

	//List<BoardFile> findByBoardIdAndFileType(Long boardId, BoardFileType fileType);

	void deleteByBoardIdAndFileType(Long boardId, BoardFileType fileType);
}
