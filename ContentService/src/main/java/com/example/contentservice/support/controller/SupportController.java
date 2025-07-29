package com.example.contentservice.support.controller;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/support")
public class SupportController {

  /* (TODO) 수정 & 삭제 는 isReplied 가 false 일 경우만 가능하도록 구현 */

  private final SupportService supportService;

  // 게시물 등록
  @PostMapping
  public ResponseEntity<SupportResponseDto> createSupport(@RequestBody SupportRequestDto requestDto,
      @RequestParam Long userId) {
    SupportResponseDto responseDto = supportService.createSupport(requestDto, userId);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  // 게시물 단건 조회
  @GetMapping("/{supportId}")
  public ResponseEntity<SupportDetailResponseDto> getSupport(@PathVariable Long supportId) {
    SupportDetailResponseDto responseDto = supportService.getSupport(supportId);
    return new ResponseEntity<>(responseDto, HttpStatus.OK);
  }

  // 게시물 다건 조회 -> 비공개/공개 적용 안하는 경우 관리자 인증 필요
  @GetMapping
  public ResponseEntity<PagingDto<SupportResponseDto>> getSupportsAndPaging(
      @RequestParam(defaultValue = "0") int page) {
    PagingDto<SupportResponseDto> supports = supportService.getSupportsAndPaging(page);
    return new ResponseEntity<>(supports, HttpStatus.OK);
  }

  @DeleteMapping("/{supportId}")
  public ResponseEntity<String> deleteSupport(
      @PathVariable Long supportId) {    /* (추후) 토큰으로 관리자 인증 */
    supportService.deleteSupport(supportId);
    return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
  }
}
