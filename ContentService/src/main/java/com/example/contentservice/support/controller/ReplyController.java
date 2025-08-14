package com.example.contentservice.support.controller;

import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;
import com.example.contentservice.support.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reply")
public class ReplyController {

  private final ReplyService replyService;

  /**
   * 답글 생성
   */
  @PostMapping
  public ResponseEntity<ReplyResponseDto> createReply(
      @RequestBody ReplyRequestDto requestDto) {
    ReplyResponseDto responseDto = replyService.createReply(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  /**
   * 답글 수정
   */
  @PatchMapping
  public ResponseEntity<ReplyResponseDto> updateReply(
      @RequestBody ReplyRequestDto requestDto) {
    ReplyResponseDto responseDto = replyService.updateReply(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  /**
   * 답글 삭제
   */
  @DeleteMapping("/{supportId}")
  public ResponseEntity<String> deleteReply(@PathVariable Long supportId) {
    replyService.deleteReply(supportId);
    return new ResponseEntity<>("답글이 삭제되었습니다.", HttpStatus.OK);
  }
}
