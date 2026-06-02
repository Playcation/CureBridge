package com.example.contentservice.notice.service;

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
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.config.UserClient;
import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.entity.Notice;
import com.example.contentservice.notice.repository.NoticeRepository;
import com.example.contentservice.notice.repository.NoticeSearchRepository;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

	@Mock
	private NoticeRepository noticeRepository;

	@Mock
	private NoticeSearchRepository noticeSearchRepository;

	@Mock
	private FileService fileService;

	@Mock
	private BoardFileRepository boardFileRepository;

	@Mock
	private UserClient userClient;

	@InjectMocks
	private NoticeServiceImpl noticeService;

	@Test
	@DisplayName("공지 생성 - 본문 이미지 <img> 태그가 업로드된 S3 URL 경로로 정상 매칭 및 인덱싱되는지 검증")
	void createNotice_Success_WithImagesAndFiles_Test() {
		// Given
		Long userId = 1L;
		NoticeRequestDto requestDto = new NoticeRequestDto("테스트 공지", "본문 내용 <img src='temp.png'> 입니다.");

		MockMultipartFile imageFile = new MockMultipartFile("contentImage", "img.png", "image/png",
			"raw-data".getBytes());
		List<MultipartFile> contentImages = List.of(imageFile);

		FileResponseDto fileResponse = FileResponseDto.builder()
			.fileId(100L)
			.filePath("https://s3.amazonaws.com/bucket/img.png")
			.build();

		when(fileService.uploadFiles(contentImages)).thenReturn(
			CompletableFuture.completedFuture(List.of(fileResponse)));

		Notice mockNotice = Notice.builder()
			.id(10L)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.userId(userId)
			.build();

		when(noticeRepository.save(any(Notice.class))).thenReturn(mockNotice);

		UserResponseDto mockUser = mock(UserResponseDto.class);
		when(mockUser.getName()).thenReturn("관리자");
		when(userClient.getUserInfoById(userId)).thenReturn(mockUser);

		NoticeResponseDto response = noticeService.createNotice(userId, requestDto, null, contentImages);

		// Then
		assertNotNull(response);
		assertEquals(10L, response.getNoticeId());
		assertEquals("관리자", response.getWriterName());
		assertNotNull(response.getContent());
		assertTrue(response.getContent().contains("https://s3.amazonaws.com/bucket/img.png"));

		verify(fileService, times(1)).uploadFiles(contentImages);
		verify(noticeRepository, times(1)).save(any(Notice.class));
		verify(boardFileRepository, times(1)).saveAll(anyList());
		verify(noticeSearchRepository, times(1)).save(any(NoticeDocument.class));
		verify(userClient, times(1)).getUserInfoById(userId);
	}

	@Test
	@DisplayName("공지 상세 조회 - FeignClient 예외가 터져도 전체 로직이 터지지 않고 '작성자' 폴백 명이 정상 출력된다")
	void getNotice_UserClientException_FallbackToDefaultWriter_Test() {
		// Given
		Long noticeId = 10L;
		Notice mockNotice = Notice.builder()
			.id(noticeId)
			.title("공지")
			.content("내용")
			.userId(5L)
			.viewCount(0L)
			.build();

		when(noticeRepository.findByIdOrElseThrow(noticeId)).thenReturn(mockNotice);
		when(userClient.getUserInfoById(5L)).thenThrow(new RuntimeException("Feign Connection Timeout"));

		NoticeResponseDto response = noticeService.getNotice(noticeId);

		// Then
		assertNotNull(response);
		assertEquals("작성자", response.getWriterName()); // try-catch 방어벽 작동 확인
		verify(noticeRepository, times(1)).findByIdOrElseThrow(noticeId);
		verify(userClient, times(1)).getUserInfoById(5L);
		verify(noticeRepository, times(1)).save(mockNotice);
	}
}