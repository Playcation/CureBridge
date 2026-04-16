package com.example.contentservice.support.service;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {

  private final SupportRepository supportRepository;

  @Override
  @Transactional
  public ReplyResponseDto createReply(ReplyRequestDto requestDto) {
    Support support = supportRepository.findByIdOrElseThrow(requestDto.getSupportId());

    if (support.isReplied()) {
      throw new NotFoundException(BoardErrorCode.EXIST_REPLY);
    }

    support.addReply(requestDto.getReplyContent());

    return ReplyResponseDto.toDto(support);
  }

  @Override
  @Transactional
  public ReplyResponseDto updateReply(ReplyRequestDto requestDto) {
    Support support = supportRepository.findByIdOrElseThrow(requestDto.getSupportId());

    if (!support.isReplied()) {
      throw new IllegalStateException("아직 답글이 작성되지 않은 문의입니다.");
    }

    support.updateReply(requestDto.getReplyContent());
    Support updatedSupport = supportRepository.save(support);

    return ReplyResponseDto.toDto(updatedSupport);
  }

  @Override
  @Transactional
  public void deleteReply(Long supportId) {
    Support support = supportRepository.findByIdOrElseThrow(supportId);

    if (!support.isReplied()) {
      throw new NotFoundException(BoardErrorCode.NOT_FOUND_REPLY);
    }

    support.deleteReply();

    supportRepository.save(support);
  }
}