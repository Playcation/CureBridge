package com.example.contentservice.support.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.support.dto.ReplyRequestDto;
import com.example.contentservice.support.dto.ReplyResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;

@ExtendWith(MockitoExtension.class)
class ReplyServiceImplTest {

	@Mock
	private SupportRepository supportRepository;

	@InjectMocks
	private ReplyServiceImpl replyService;

	@Test
	@DisplayName("답글 생성 - 신규 답변이 완수되면 답변유무 플래그가 true로 수동 전환된다")
	void createReply_Success_ChangesSupportState_Test() {
		// Given
		Long supportId = 100L;
		Support pureSupport = Support.builder().id(supportId).isReplied(false).build();
		when(supportRepository.findByIdOrElseThrow(supportId)).thenReturn(pureSupport);

		// 요청 생성
		ReplyRequestDto requestDto = mock(ReplyRequestDto.class);
		when(requestDto.getSupportId()).thenReturn(supportId);
		when(requestDto.getReplyContent()).thenReturn("관리자 정성 답변입니다.");

		// When
		ReplyResponseDto response = replyService.createReply(requestDto);

		// Then
		assertNotNull(response);
		assertTrue(response.isReplied());
		assertEquals("관리자 정성 답변입니다.", pureSupport.getReplyContent());
	}

	@Test
	@DisplayName("답글 생성 제약 - 이미 기존 답변이 있는 글에 중복 생성 시 예외 발생")
	void createReply_AlreadyExistReply_ThrowsException() {
		// Given
		Long supportId = 100L;
		Support alreadyRepliedSupport = Support.builder()
			.id(supportId)
			.isReplied(true)
			.build();

		when(supportRepository.findByIdOrElseThrow(supportId)).thenReturn(alreadyRepliedSupport);

		ReplyRequestDto requestDto = mock(ReplyRequestDto.class);
		when(requestDto.getSupportId()).thenReturn(supportId);

		// When & Then
		assertThrows(NotFoundException.class, () -> {
			replyService.createReply(requestDto);
		});

		verify(supportRepository, never()).save(any(Support.class));
	}

	@Test
	@DisplayName("답글 삭제 - 복구 진행 시 답변 내용 소멸 및 롤백")
	void deleteReply_RollbackState_Test() {
		// Given
		Long supportId = 100L;
		Support repliedSupport = Support.builder()
			.id(supportId)
			.isReplied(true)
			.replyContent("기존 답변")
			.build();

		when(supportRepository.findByIdOrElseThrow(supportId)).thenReturn(repliedSupport);

		// When
		replyService.deleteReply(supportId);

		// Then
		assertFalse(repliedSupport.isReplied());
		assertNull(repliedSupport.getReplyContent());
		verify(supportRepository, times(1)).findByIdOrElseThrow(supportId);
		verify(supportRepository, times(1)).save(repliedSupport);
	}
}