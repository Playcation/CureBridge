package com.example.contentservice.support.service;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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

    Support updatedSupport = Support.builder()
        .id(support.getId()) // 기존 ID 유지
        .title(support.getTitle())
        .content(support.getContent())
        .userId(support.getUserId())
        .isPrivate(support.isPrivate())
        .isReplied(true)
        .replyContent(requestDto.getReplyContent())
        .repliedAt(LocalDateTime.now())
        .build();
    Support savedSupport = supportRepository.save(updatedSupport);

    return ReplyResponseDto.toDto(savedSupport);
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