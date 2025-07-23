package com.example.contentservice.notice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.NoticeSearchResponseDto;
import com.example.contentservice.notice.service.NoticeSearchService;
import com.example.contentservice.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {
	private final NoticeService noticeService;
	private final NoticeSearchService noticeSearchService;

	// 게시물 등록
	@PostMapping
	public ResponseEntity<NoticeResponseDto> createNotice(@RequestBody NoticeRequestDto requestDto,
		@RequestParam Long userId) {    /* (추후) 토큰으로 관리자 인증 */
		NoticeResponseDto responseDto = noticeService.createNotice(requestDto, userId);
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
	public ResponseEntity<PagingDto<NoticeResponseDto>> getNoticesAndPaging(
		@RequestParam(defaultValue = "0") int page) {
		PagingDto<NoticeResponseDto> notices = noticeService.getBoardsAndPaging(page);
		return new ResponseEntity<>(notices, HttpStatus.OK);
	}

	// 게시물 수정
	@PatchMapping("/{noticeId}")
	public ResponseEntity<NoticeResponseDto> updateNotice(@PathVariable Long noticeId,
		@RequestBody NoticeRequestDto requestDto) {    /* (추후) 토큰으로 관리자 인증 */
		NoticeResponseDto responseDto = noticeService.updateNotice(noticeId, requestDto);
		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}

	@DeleteMapping("/{noticeId}")
	public ResponseEntity<String> deleteNotice(@PathVariable Long noticeId) {    /* (추후) 토큰으로 관리자 인증 */
		noticeService.deleteNotice(noticeId);
		return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
	}

	@GetMapping("/search-title")
	public ResponseEntity<List<NoticeSearchResponseDto>> searchByTitle(
		@RequestParam("keyword") String keyword) {

		List<NoticeSearchResponseDto> result = noticeSearchService.searchByTitle(keyword);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/search-all")
	public ResponseEntity<List<NoticeSearchResponseDto>> searchByAll(
		@RequestParam("keyword") String keyword) {

		List<NoticeSearchResponseDto> result = noticeSearchService.searchByAll(keyword);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
