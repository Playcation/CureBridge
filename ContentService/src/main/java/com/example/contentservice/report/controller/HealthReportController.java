package com.example.contentservice.report.controller;

import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrMultiResponseDto;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportMultiResponseDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import com.example.contentservice.report.service.HealthReportService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/health-report")
@AllArgsConstructor
public class HealthReportController {

  private final HealthReportService healthReportService;

  @PostMapping("/create/user/{userId}")
  private ResponseEntity<HealthReportResponseDto> uploadOcrFile(
      @PathVariable Long userId,
      @RequestBody CreateHealthReportRequestDto createHealthReportRequestDto
  ) {
    return ResponseEntity.ok().body(healthReportService.createHealthReport(userId, createHealthReportRequestDto));
  }

  @GetMapping("/user/{userId}")
  private ResponseEntity<List<HealthReportMultiResponseDto>> getOcrResult(
//      @RequestHeader("Authorization") String authorizationHeader
      @PathVariable Long userId
  ) {
    return ResponseEntity.ok().body(healthReportService.getHealthReport(userId));
  }

  @GetMapping("/{id}")
  private ResponseEntity<HealthReportResponseDto> getOcrDetailResult(
      @PathVariable("id") String id
  ){
    return ResponseEntity.ok().body(healthReportService.getHealthReportDetail(id));
  }

  @PutMapping("/{id}")
  private ResponseEntity<HealthReportResponseDto> updateOcrResult(
      @PathVariable("id") String id,
      @RequestBody UpdateHealthReportRequestDto updateHealthReportRequestDto
  ){
    return ResponseEntity.ok().body(healthReportService.updateHealthReport(id, updateHealthReportRequestDto));
  }

  @DeleteMapping("/{id}")
  private ResponseEntity<String> deleteOcrResult(
      @PathVariable("id") String id,
      @RequestBody DeleteHealthReportRequestDto deleteHealthReportRequestDto
  ){
    return ResponseEntity.ok().body(healthReportService.deleteHealthReport(id, deleteHealthReportRequestDto));
  }

}
