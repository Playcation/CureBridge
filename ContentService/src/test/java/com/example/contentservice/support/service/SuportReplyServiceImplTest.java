package com.example.contentservice.support.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.repository.FileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import com.example.contentservice.support.repository.SupportSearchRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SupportReplyServiceImplTest {

  @Mock
  private SupportRepository supportRepository;
  @Mock
  private SupportSearchRepository supportSearchRepository;
  @Mock
  private BoardFileRepository boardFileRepository;
  @Mock
  private FileService fileService;
  @Mock
  private FileRepository fileRepository;

  @InjectMocks
  private SupportServiceImpl supportService;

  @InjectMocks
  private ReplyServiceImpl replyService;

  private Support testSupport;

  @BeforeEach
  void setUp() {
    testSupport = Support.builder()
        .id(1L)
        .title("테스트 문의")
        .content("내용입니다.")
        .userId(100L)
        .viewCount(0L)
        .isReplied(false)
        .build();
  }

  // -------------------------------------------------------------------------
  // 1. Support (문의사항) 서비스 테스트
  // -------------------------------------------------------------------------

  @Test
  @DisplayName("문의사항 수정 실패 - 본인이 아닌 경우")
  void updateSupport_Fail_InvalidOwner() {
    // given
    SupportRequestDto requestDto = new SupportRequestDto("수정 제목", "수정 내용", false, false);
    given(supportRepository.findByIdOrElseThrow(1L)).willReturn(testSupport);

    // when & then
    // 작성자 ID는 100L인데 수정을 시도하는 ID는 999L인 경우
    assertThrows(NoAuthorizedException.class, () ->
        supportService.updateSupport(1L, 999L, requestDto)
    );
  }

  @Test
  @DisplayName("문의사항 수정 실패 - 이미 답글이 달린 경우")
  void updateSupport_Fail_AlreadyReplied() {
    // given
    testSupport = Support.builder().id(1L).userId(100L).isReplied(true).build();
    SupportRequestDto requestDto = new SupportRequestDto("수정 제목", "수정 내용", true, false);
    given(supportRepository.findByIdOrElseThrow(1L)).willReturn(testSupport);

    // when & then
    // 이미 답글이 달린 문의는 수정할 수 없음
    assertThrows(InvalidInputException.class, () ->
        supportService.updateSupport(1L, 100L, requestDto)
    );
  }

  // -------------------------------------------------------------------------
  // 2. Reply (답글) 서비스 테스트
  // -------------------------------------------------------------------------

  @Test
  @DisplayName("답글 생성 성공 - 상태가 isReplied=true로 변경되는지 확인")
  void createReply_Success() {
    // given
    ReplyRequestDto requestDto = mock(ReplyRequestDto.class);
    given(requestDto.getSupportId()).willReturn(1L);
    given(requestDto.getReplyContent()).willReturn("문의에 대한 답변입니다.");

    given(supportRepository.findByIdOrElseThrow(1L)).willReturn(testSupport);

    // save 시 반환될 Support 객체 (답글이 달린 상태)
    given(supportRepository.save(any(Support.class))).willAnswer(
        invocation -> invocation.getArgument(0));

    // when
    ReplyResponseDto response = replyService.createReply(requestDto);

    // then
    assertTrue(response.isReplied());
    assertEquals("문의에 대한 답변입니다.", response.getReplyContent());
    verify(supportRepository, times(1)).save(any(Support.class));
  }

  @Test
  @DisplayName("답글 삭제 성공 - support 엔티티의 reply 정보가 초기화되는지 확인")
  void deleteReply_Success() {
    // given
    // 답글이 이미 존재하는 Support 객체 생성
    testSupport = Support.builder()
        .id(1L)
        .isReplied(true)
        .replyContent("기존 답변")
        .repliedAt(LocalDateTime.now())
        .build();

    given(supportRepository.findByIdOrElseThrow(1L)).willReturn(testSupport);

    // when
    replyService.deleteReply(1L);

    // then
    // deleteReply 호출 시 내부 필드가 null 또는 false로 초기화됨
    assertFalse(testSupport.isReplied());
    assertNull(testSupport.getReplyContent());
    verify(supportRepository, times(1)).save(testSupport);
  }
}