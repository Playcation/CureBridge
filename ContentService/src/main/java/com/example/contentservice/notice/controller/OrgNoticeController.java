package com.example.contentservice.notice.controller;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.utils.JwtParser;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import com.example.contentservice.notice.service.NoticeSearchService;
import com.example.contentservice.notice.service.OrgNoticeService;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orgs/{orgId}/notice")
public class OrgNoticeController {

  private final OrgNoticeService noticeOrgService;          // 조직 공지사항 관련 비즈니스 로직 처리 서비스
  private final NoticeSearchService noticeSearchService;    // 공지사항 검색 기능을 제공하는 서비스
  private final JwtParser jwtParser;                        // JWT 토큰 파싱 및 권한 검증 유틸

  // 조직 공지사항 생성
  // multipart/form-data를 사용하며 JSON 데이터와 첨부 파일을 함께 수신함
  // 조직 관리자 또는 상위 관리자 권한이 필요한 API
  @PostMapping
  public ResponseEntity<NoticeResponseDto> createNotice(
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @PathVariable Long orgId,
      @RequestPart(value = "json") NoticeRequestDto requestDto,
      @RequestPart(value = "attachedFile", required = false) List<MultipartFile> attachedFiles,
      @RequestPart(value = "contentImage", required = false) List<MultipartFile> contentImages) {

    Long userId = jwtParser.findUserByToken(authorizationHeader);   // 토큰에서 사용자 ID 파싱
    jwtParser.checkOrgOrManager(authorizationHeader);                 // 조직 관리자 또는 상위 관리자 권한 검증

    NoticeResponseDto responseDto = noticeOrgService.createOrgNotice(
        userId, orgId, requestDto, attachedFiles, contentImages);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 조직 공지사항 단건 조회
  // 조직 ID와 공지사항 ID를 기준으로 조회한다
  @GetMapping("/{noticeId}")
  public ResponseEntity<NoticeResponseDto> getNotice(@PathVariable Long orgId,
      @PathVariable Long noticeId) {
    NoticeResponseDto responseDto = noticeOrgService.getOrgNotice(orgId, noticeId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 조직 공지사항 목록 조회 (페이징 적용)
  @GetMapping
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> getNoticesAndPaging(
      @PathVariable Long orgId,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

    PagingDto<PagingNoticeResponseDto> notices =
        noticeOrgService.getOrgNoticesAndPaging(orgId, pageable);

    return new ResponseEntity<>(notices, HttpStatus.OK);
  }

  // 조직 공지사항 수정
  // multipart가 아닌 JSON 데이터 기반 수정
  // 관리자 권한 검증이 필요함
  @PatchMapping("/{noticeId}")
  public ResponseEntity<NoticeResponseDto> updateNotice(
      @PathVariable Long orgId,
      @PathVariable Long noticeId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader,
      @RequestPart(value = "json") NoticeRequestDto requestDto) {

    jwtParser.checkOrgOrManager(authorizationHeader);   // 권한 검사

    NoticeResponseDto responseDto =
        noticeOrgService.updateOrgNotice(orgId, noticeId, requestDto);

    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 조직 공지사항 삭제
  // 조직 관리자 또는 상위 관리자 권한이 필요함
  @DeleteMapping("/{noticeId}")
  public ResponseEntity<String> deleteNotice(
      @PathVariable Long orgId,
      @PathVariable Long noticeId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {

    jwtParser.checkOrgOrManager(authorizationHeader);   // 권한 검사
    noticeOrgService.deleteOrgNotice(orgId, noticeId);

    return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
  }

  // 제목 기반 검색
  // 전체 공지사항 대상 검색이며, 조직 제한은 적용하지 않음
  @GetMapping("/search-title")
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> searchByTitleAndPaging(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

    PagingDto<PagingNoticeResponseDto> result =
        noticeSearchService.searchByTitle(keyword, pageable);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 제목 + 내용 검색
  // 검색 범위는 전체 공지사항, 조직 단위 제한 없음
  @GetMapping("/search-all")
  public ResponseEntity<PagingDto<PagingNoticeResponseDto>> searchByAll(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

    PagingDto<PagingNoticeResponseDto> result =
        noticeSearchService.searchByAll(keyword, pageable);

    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}