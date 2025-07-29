package com.example.contentservice.support.service;

import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;

public interface ReplyService {

  /**
   * 답글 생성
   */
  ReplyResponseDto createReply(ReplyRequestDto requestDto);

  /**
   * 답글 수정
   */
  ReplyResponseDto updateReply(ReplyRequestDto requestDto);

  /**
   * 답글 삭제
   */
  void deleteReply(Long supportId);
}