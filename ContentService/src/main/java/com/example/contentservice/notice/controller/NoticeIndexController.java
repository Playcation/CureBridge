package com.example.contentservice.notice.controller;

import com.example.contentservice.notice.service.NoticeIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice/index")
@RequiredArgsConstructor
public class NoticeIndexController {

  // 공지사항 Elasticsearch 인덱스를 생성하거나 초기화하는 서비스
  private final NoticeIndexService noticeIndexService;

  // 공지사항 인덱스를 수동으로 생성하는 API
  // 일반적으로 서비스 초기 설정 또는 인덱스 재생성이 필요할 때 사용된다
  @PostMapping("/create")
  public ResponseEntity<String> createNoticeIndex() {
    // 인덱스 생성 결과 메시지 반환
    String result = noticeIndexService.createNoticeIndex();
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}