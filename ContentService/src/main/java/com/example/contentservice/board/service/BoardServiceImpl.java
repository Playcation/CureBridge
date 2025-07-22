package com.example.contentservice.board.service;

import com.example.contentservice.board.dto.BoardRequestDto;
import com.example.contentservice.board.dto.BoardResponseDto;
import com.example.contentservice.board.entity.BoardType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BoardServiceImpl implements BoardService {

  @Override
  public BoardResponseDto createBoard(BoardRequestDto dto) {
    return null;
  }

  @Override
  public BoardResponseDto getBoard(Long boardId) {
    return null;
  }

  @Override
  public List<BoardResponseDto> getBoards(BoardType boardType) {
    return List.of();
  }

  @Override
  public BoardResponseDto updateBoard(Long boardId, BoardRequestDto dto) {
    return null;
  }

  @Override
  public void deleteBoard(Long boardId) {

  }
}
