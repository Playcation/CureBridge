package com.example.contentservice.notice.controller;

import com.example.contentservice.notice.service.NoticeIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices/index")
@RequiredArgsConstructor
public class NoticeIndexController {

  private final NoticeIndexService noticeIndexService;

  @PostMapping("/create")
  public ResponseEntity<String> createNoticeIndex() {
    String result = noticeIndexService.createNoticeIndex();
    return new ResponseEntity<>(result, HttpStatus.OK);

  }
}