package com.example.contentservice.ocr.controller;

import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import com.example.contentservice.ocr.dto.UploadRequestDto;
import com.example.contentservice.ocr.service.OcrService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/api/ocr")
@AllArgsConstructor
public class OcrController {

  private final OcrService ocrService;

  @PostMapping("/upload")
  private ResponseEntity<String> UploadOcrFile(
      @RequestPart("json") UploadRequestDto uploadRequestDto,
      @RequestPart MultipartFile file
  ){
    // ToDo : 단일 증명서 파일 업로드.
    return null;
  }

  @GetMapping("/user")
  private ResponseEntity<OcrResponseDto[]> getOcrResult() {
    // ToDo: 개인 레포트 조회
    return null;
  }

  @GetMapping("/{id}")
  private ResponseEntity<OcrResponseDto> getOcrDetailResult(
      @PathVariable("id") Long id
  ){
    // TODO : 단일 OCR 결과 조회
    return null;
  }

  @PutMapping("/{id}")
  private ResponseEntity<String> updateOcrResult(
      @PathVariable("id") Long id,
      @RequestBody UpdateRequestDto updateRequestDto
  ){
    // TODO : 결과 수정
    return null;
  }

  @DeleteMapping("/{id}")
  private ResponseEntity<String> deleteOcrResult(
      @PathVariable("id") Long id,
      @RequestBody DeleteRequestDto deleteRequestDto
  ){
    // TODO : 결과 삭제
    return null;
  }

}
