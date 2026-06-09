package com.example.contentservice.support.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.commonmodule.dto.UserResponseDto;
import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.config.UserClient;
import com.example.contentservice.support.document.SupportDocument;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import com.example.contentservice.support.repository.SupportSearchRepository;

@ExtendWith(MockitoExtension.class)
class SupportServiceImplTest {

	@Mock
	private SupportRepository supportRepository;

	@Mock
	private SupportSearchRepository supportSearchRepository;

	@Mock
	private BoardFileRepository boardFileRepository;

	@Mock
	private FileService fileService;

	@Mock
	private UserClient userClient;

	@InjectMocks
	private SupportServiceImpl supportService;

	@Test
	@DisplayName("문의 생성 - 첨부파일 S3 업로드 연동 검증")
	void createSupport_Success_Test() {
		// Given
		Long userId = 1L;
		SupportRequestDto requestDto = new SupportRequestDto("문의 제목", "문의 내용", false, false);
		MockMultipartFile multipartFile = new MockMultipartFile("attachedFile", "doc.pdf", "application/pdf",
			"data".getBytes());
		List<MultipartFile> attachedFiles = List.of(multipartFile);

		FileResponseDto fileResponse = FileResponseDto.builder()
			.fileId(500L)
			.filePath("https://s3.path/doc.pdf")
			.build();

		when(fileService.uploadFiles(attachedFiles)).thenReturn(
			CompletableFuture.completedFuture(List.of(fileResponse)));

		Support mockSupport = Support.builder()
			.id(10L)
			.title("문의 제목")
			.content("문의 내용")
			.userId(userId)
			.build();

		when(supportRepository.save(any(Support.class))).thenReturn(mockSupport);

		UserResponseDto mockUser = mock(UserResponseDto.class);
		when(mockUser.getName()).thenReturn("홍길동");
		when(userClient.getUserInfoById(userId)).thenReturn(mockUser);

		// When
		SupportResponseDto response = supportService.createSupport(requestDto, userId, attachedFiles);

		// Then
		assertNotNull(response);
		assertEquals(10L, response.getSupportId());
		assertEquals("홍길동", response.getWriterName());
		assertTrue(response.getAttachedFilePaths().contains("https://s3.path/doc.pdf"));

		verify(fileService, times(1)).uploadFiles(attachedFiles);
		verify(supportRepository, times(1)).save(any(Support.class));
		verify(boardFileRepository, times(1)).saveAll(anyList());
		verify(supportSearchRepository, times(1)).save(any(SupportDocument.class));
		verify(userClient, times(1)).getUserInfoById(userId);
	}

	@Test
	@DisplayName("문의 조회 보안벽 - 비공개 글을 제3자가 실행할 경우 NoAuthorizedException 발생")
	void getSupport_PrivatePost_StrangerAccess_ThrowsException() {
		// Given
		Long supportId = 10L;
		Long ownerId = 7L;
		Long strangerId = 99L; // 제3자 로그인 유저 ID

		Support privateSupport = Support.builder()
			.id(supportId)
			.userId(ownerId)
			.isPrivate(true)
			.build();
		when(supportRepository.findByIdOrElseThrow(supportId))
			.thenReturn(privateSupport);

		// When & Then
		assertThrows(NoAuthorizedException.class, () -> {
			supportService.getSupport(supportId, strangerId, "ROLE_USER");
		});

		// 예외가 안전하게 가로챘으므로 나중 조회수 증가 반영 로직은 작동하지 않아야 함
		verify(supportRepository, times(1)).findByIdOrElseThrow(supportId);
		verify(supportRepository, never()).save(any(Support.class));
		verify(boardFileRepository, never()).findByBoardId(anyLong());
		verify(userClient, never()).getUserInfoById(anyLong());
	}

	@Test
	@DisplayName("문의 조회 - 비공개 글이더라도 관리자(ADMIN) 권한이거나 본인이 접근하면 정상 수행")
	void getSupport_PrivatePost_AdminOrOwnerAccess_Success() {
		// Given
		Long supportId = 10L;
		Long ownerId = 7L;

		Support privateSupport = Support.builder()
			.id(supportId)
			.title("비공개 문의")
			.content("내용")
			.userId(ownerId)
			.isPrivate(true)
			.viewCount(0L)
			.build();

		when(supportRepository.findByIdOrElseThrow(supportId)).thenReturn(privateSupport);

		when(boardFileRepository.findByBoardId(supportId)).thenReturn(List.of());

		UserResponseDto mockUser = mock(UserResponseDto.class);
		when(mockUser.getName()).thenReturn("작성자명");
		when(userClient.getUserInfoById(ownerId)).thenReturn(mockUser);

		// When
		SupportDetailResponseDto response = supportService.getSupport(supportId, 99L, "ADMIN");

		// Then
		assertNotNull(response);
		assertEquals("작성자명", response.getWriterName());
		assertEquals(1L, response.getViewCount());

		verify(supportRepository, times(1)).findByIdOrElseThrow(supportId);
		verify(supportRepository, times(1)).save(privateSupport);
		verify(boardFileRepository, times(1)).findByBoardId(supportId);
		verify(userClient, times(1)).getUserInfoById(ownerId);
	}

	@Test
	@DisplayName("문의 수정 제약 - 이미 관리자 답변이 채택 완료된 글은 수정 불가능")
	void updateSupport_AlreadyReplied_ThrowsException() {
		// Given
		Long supportId = 10L;
		Long userId = 1L;

		// isReplied가 이미 true 상태인 가상 영속 엔티티 바인딩
		Support repliedSupport = Support.builder()
			.id(supportId)
			.userId(userId)
			.isReplied(true)
			.build();

		when(supportRepository.findByIdOrElseThrow(supportId)).thenReturn(repliedSupport);

		SupportRequestDto updateDto = new SupportRequestDto("수정제목", "수정내용", false, false);

		// When & Then
		assertThrows(InvalidInputException.class, () -> {
			supportService.updateSupport(supportId, userId, updateDto);
		});

		verify(supportRepository, times(1)).findByIdOrElseThrow(supportId);
		verify(supportRepository, never()).save(any(Support.class));
		verify(supportSearchRepository, never()).save(any(SupportDocument.class));
	}
}