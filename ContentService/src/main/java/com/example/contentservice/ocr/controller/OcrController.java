package com.example.contentservice.ocr.controller;

import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrMultiResponseDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/ocr")
@AllArgsConstructor
public class OcrController {

  private final OcrService ocrService;

  /**
   * OCR 파일 업로드후 분석
   *
   * @param file
   * @return
   */
  @PostMapping("/upload/user/{userId}")
  private ResponseEntity<OcrResponseDto> uploadOcrFile(
      @PathVariable Long userId,
      @RequestPart("file") MultipartFile file
  ) {
    return ResponseEntity.ok().body(ocrService.analyzeImageWithClovaOcr(userId, file));
  }

  @GetMapping("/user/{userId}")
  private ResponseEntity<List<OcrMultiResponseDto>> getOcrResult(
//      @RequestHeader("Authorization") String authorizationHeader
      @PathVariable Long userId
  ) {
    return ResponseEntity.ok().body(ocrService.getOcrResult(userId));
  }

  @GetMapping("/{id}")
  private ResponseEntity<OcrResponseDto> getOcrDetailResult(
      @PathVariable("id") Long id
  ){
    return ResponseEntity.ok().body(ocrService.getOcrDetalResult(id));
  }

  @PutMapping("/{id}")
  private ResponseEntity<OcrResponseDto> updateOcrResult(
      @PathVariable("id") Long id,
      @RequestBody UpdateRequestDto updateRequestDto
  ){
    return ResponseEntity.ok().body(ocrService.updateOcrResult(id, updateRequestDto));
  }

  @DeleteMapping("/{id}")
  private ResponseEntity<String> deleteOcrResult(
      @PathVariable("id") Long id,
      @RequestBody DeleteRequestDto deleteRequestDto
  ){
    // TODO : 결과 삭제
    return ResponseEntity.ok().body(ocrService.deleteOcrResult(id, deleteRequestDto));
  }

}
