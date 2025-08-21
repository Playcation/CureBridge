package com.example.contentservice.notice.controller;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import com.example.contentservice.notice.service.NoticeSearchService;
import com.example.contentservice.notice.service.NoticeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

  private final NoticeService noticeService;
  private final NoticeSearchService noticeSearchService;

  // 게시물 등록
  @PostMapping
  public ResponseEntity<NoticeResponseDto> createNotice(@RequestParam Long userId,
      @RequestPart(value = "json") NoticeRequestDto requestDto,
      @RequestPart(value = "attachedFile", required = false) List<MultipartFile> attachedFiles,
      @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages) {    /* (TODO) 토큰으로 관리자 인증 */
    NoticeResponseDto responseDto = noticeService.createNotice(userId, requestDto, attachedFiles,
        contentImages);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 게시물 단건 조회
  @GetMapping("/{noticeId}")
  public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long noticeId) {
    NoticeResponseDto responseDto = noticeService.getNotice(noticeId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 게시물 다건 조회
  @GetMapping
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> getNoticesAndPaging(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    PagingDto<PagingNoticeResponseDto> notices = noticeService.getNoticesAndPaging(
        pageable);
    return new ResponseEntity<>(notices, HttpStatus.OK);
  }

  // 게시물 수정
  @PatchMapping("/{noticeId}")
  public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long noticeId,
      @RequestPart(value = "json") NoticeRequestDto requestDto) {    /* (TODO) 토큰으로 관리자 인증 */
    NoticeResponseDto responseDto = noticeService.updateNotice(noticeId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 게시물 삭제
  @DeleteMapping("/{noticeId}")
  public ResponseEntity<String> deleteNotice(
      @PathVariable Long noticeId) {    /* (TODO) 토큰으로 관리자 인증 */
    noticeService.deleteNotice(noticeId);
    return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
  }

  // 제목 으로 검색
  @GetMapping("/search-title")
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> searchByTitleAndPaging(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    PagingDto<PagingNoticeResponseDto> result = noticeSearchService.searchByTitle(keyword,
        pageable);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 제목+내용 으로 검색
  @GetMapping("/search-all")
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> searchByAll(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    PagingDto<PagingNoticeResponseDto> result = noticeSearchService.searchByAll(keyword, pageable);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
