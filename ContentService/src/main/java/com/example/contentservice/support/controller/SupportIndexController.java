package com.example.contentservice.support.controller;

import com.example.contentservice.support.service.SupportIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support/index")
@RequiredArgsConstructor
public class SupportIndexController {

  private final SupportIndexService supportIndexService;

  @PostMapping("/create")
  public ResponseEntity<String> createSupportIndex() {
    String result = supportIndexService.createSupportIndex();
    return new ResponseEntity<>(result, HttpStatus.OK);

  }
}