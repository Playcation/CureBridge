package com.example.contentservice.support.controller;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.utils.JwtParser;
import com.example.contentservice.support.dto.PagingSupportResponseDto;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.service.SupportSearchService;
import com.example.contentservice.support.service.SupportService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/support")
public class SupportController {

  /**
   * 수정 & 삭제 는 isReplied 가 false 일 경우만 가능하도록 구현
   */

  private final SupportService supportService;
  private final SupportSearchService supportSearchService;
  private final JwtParser jwtParser;

  // 게시물 등록
  @PostMapping
  public ResponseEntity<SupportResponseDto> createSupport(
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestPart(value = "json") SupportRequestDto requestDto,
      @RequestPart(value = "attachedFile", required = false) List<MultipartFile> attachedFiles) {
    Long userId = jwtParser.findUserByToken(authorizationHeader);
    SupportResponseDto responseDto = supportService.createSupport(requestDto, userId,
        attachedFiles);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 게시물 단건 조회
  @GetMapping("/{supportId}")
  public ResponseEntity<SupportDetailResponseDto> getSupport(@PathVariable Long supportId) {
    SupportDetailResponseDto responseDto = supportService.getSupport(supportId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 게시물 다건 조회
  @GetMapping
  public ResponseEntity<PagingDto<PagingSupportResponseDto>> getSupportsAndPaging(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    PagingDto<PagingSupportResponseDto> supports = supportService.getSupportsAndPaging(pageable);
    return new ResponseEntity<>(supports, HttpStatus.OK);
  }

  // 게시물 수정
  @PatchMapping("/{supportId}")
  public ResponseEntity<SupportResponseDto> updateSupport(@PathVariable Long supportId,
      @RequestHeader("Authorization") String authorizationHeader,
      @RequestBody SupportRequestDto requestDto) {
    Long userId = jwtParser.findUserByToken(authorizationHeader);
    SupportResponseDto responseDto = supportService.updateSupport(supportId, userId, requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 게시물 삭제
  @DeleteMapping("/{supportId}")
  public ResponseEntity<String> deleteSupport(
      @PathVariable Long supportId,
      @RequestHeader("Authorization") String authorizationHeader) {    /* (추후) 토큰으로 관리자 인증 */
    Long userId = jwtParser.findUserByToken(authorizationHeader);
    supportService.deleteSupport(supportId, userId);
    return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
  }

  // 제목 으로 검색
  @GetMapping("/search-title")
  public ResponseEntity<PagingDto<PagingSupportResponseDto>> searchByTitleAndPaging(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    PagingDto<PagingSupportResponseDto> result = supportSearchService.searchByTitle(keyword,
        pageable);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  // 제목+내용 으로 검색
  @GetMapping("/search-all")
  public ResponseEntity<PagingDto<PagingSupportResponseDto>> searchByAll(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    PagingDto<PagingSupportResponseDto> result = supportSearchService.searchByAll(keyword,
        pageable);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
