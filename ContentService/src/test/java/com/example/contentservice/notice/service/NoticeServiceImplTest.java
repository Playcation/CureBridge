package com.example.contentservice.notice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.repository.FileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import com.example.contentservice.notice.entity.Notice;
import com.example.contentservice.notice.repository.NoticeRepository;
import com.example.contentservice.notice.repository.NoticeSearchRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
  private FileRepository fileRepository;

  @InjectMocks
  private NoticeServiceImpl noticeService;

  private Notice savedNotice;

  @BeforeEach
  void setUp() {
    savedNotice = Notice.builder()
        .id(1L)
        .userId(10L)
        .orgId(0L)
        .title("공지 제목")
        .content("<p>공지 내용</p>")
        .viewCount(0L)
        .build();
  }

  @Test
  @DisplayName("공지 생성 - 첨부/본문이미지 없으면 Notice 저장 + ES 저장 + 빈 파일리스트 반환")
  void createNotice_NoFiles_Test() {
    // Given
    Long userId = 10L;
    NoticeRequestDto requestDto = new NoticeRequestDto("공지 제목", "<p>공지 내용</p>");

    when(noticeRepository.save(any(Notice.class))).thenReturn(savedNotice);

    // When (attachedFiles/contentImages = null 로 넣어서 업로드 로직 자체를 안 타게)
    NoticeResponseDto result = noticeService.createNotice(userId, requestDto, null, null);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.getNoticeId());
    assertEquals("공지 제목", result.getTitle());

    // 파일 경로 리스트는 빈 리스트여야 함
    assertNotNull(result.getContentImagePaths());
    assertNotNull(result.getAttachedFilePaths());
    assertEquals(0, result.getContentImagePaths().size());
    assertEquals(0, result.getAttachedFilePaths().size());

    verify(noticeRepository, times(1)).save(any(Notice.class));
    verify(noticeSearchRepository, times(1)).save(any());
    verify(boardFileRepository, never()).saveAll(any()); // 파일 없으면 저장 리스트가 비어야 함
  }

  @Test
  @DisplayName("공지 단건 조회 - 조회수 증가 후 저장 호출")
  void getNotice_IncreaseViewCount_Test() {
    // Given
    Long noticeId = 1L;
    Notice notice = Notice.builder()
        .id(noticeId)
        .userId(10L)
        .orgId(0L)
        .title("t")
        .content("c")
        .viewCount(0L)
        .build();

    when(noticeRepository.findByIdOrElseThrow(noticeId)).thenReturn(notice);
    when(boardFileRepository.findByBoardId(noticeId)).thenReturn(Collections.emptyList());

    // When
    NoticeResponseDto result = noticeService.getNotice(noticeId);

    // Then
    assertNotNull(result);
    assertEquals(noticeId, result.getNoticeId());

    // 저장될 때 viewCount가 +1 되었는지 캡처로 검증
    ArgumentCaptor<Notice> captor = ArgumentCaptor.forClass(Notice.class);
    verify(noticeRepository, times(1)).save(captor.capture());
    assertEquals(1L, captor.getValue().getViewCount());

    verify(boardFileRepository, times(1)).findByBoardId(noticeId);
  }

  @Test
  @DisplayName("공지 페이징 조회 - PagingDto 반환")
  void getNoticesAndPaging_Test() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);

    Notice n1 = Notice.builder().id(1L).title("a").userId(1L).viewCount(0L).build();
    Notice n2 = Notice.builder().id(2L).title("b").userId(1L).viewCount(5L).build();

    when(noticeRepository.findAll(pageable))
        .thenReturn(new PageImpl<>(List.of(n1, n2), pageable, 2));

    // When
    PagingDto<PagingNoticeResponseDto> result = noticeService.getNoticesAndPaging(pageable);

    // Then
    assertNotNull(result);
//    assertEquals(2L, result.getTotal());
//    assertEquals(2, result.getData().size());
//    assertEquals(1L, result.getData().get(0).getNoticeId());

    verify(noticeRepository, times(1)).findAll(pageable);
  }

  @Test
  @DisplayName("공지 수정 - 엔티티 업데이트 후 저장 + ES 저장")
  void updateNotice_Test() {
    // Given
    Long noticeId = 1L;
    Notice notice = Notice.builder()
        .id(noticeId)
        .userId(10L)
        .orgId(0L)
        .title("old")
        .content("old-content")
        .viewCount(0L)
        .build();

    NoticeRequestDto requestDto = new NoticeRequestDto("new", "new-content");

    when(noticeRepository.findByIdOrElseThrow(noticeId)).thenReturn(notice);
    when(noticeRepository.save(any(Notice.class))).thenReturn(notice);
    when(boardFileRepository.findByBoardId(noticeId)).thenReturn(Collections.emptyList());

    // When
    NoticeResponseDto result = noticeService.updateNotice(noticeId, requestDto);

    // Then
    assertNotNull(result);
    assertEquals("new", result.getTitle());
    assertEquals("new-content", result.getContent());

    verify(noticeRepository, times(1)).findByIdOrElseThrow(noticeId);
    verify(noticeRepository, times(1)).save(any(Notice.class));
    verify(noticeSearchRepository, times(1)).save(
        any()); // NoticeDocument.fromEntity(updatedNotice)
  }

  @Test
  @DisplayName("공지 삭제 - DB 삭제 + ES 삭제")
  void deleteNotice_Test() {
    // Given
    Long noticeId = 1L;
    when(noticeRepository.findByIdOrElseThrow(noticeId)).thenReturn(savedNotice);

    // When
    noticeService.deleteNotice(noticeId);

    // Then
    verify(noticeRepository, times(1)).findByIdOrElseThrow(noticeId);
    verify(noticeRepository, times(1)).deleteById(noticeId);
    verify(noticeSearchRepository, times(1)).deleteById(String.valueOf(noticeId));
  }
}