package com.example.contentservice.news.controller;

import com.example.contentservice.news.service.NewsIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news/index")
@RequiredArgsConstructor
public class NewsIndexController {

  private final NewsIndexService newsIndexService;

  @PostMapping("/create")
  public ResponseEntity<String> createNewsIndex() {
    String result = newsIndexService.createNewsIndex();
    return new ResponseEntity<>(result, HttpStatus.OK);

  }
}