package com.example.contentservice.report.service;

import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportMultiResponseDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import java.util.List;

public interface HealthReportService {

  HealthReportResponseDto createHealthReport(Long userId, CreateHealthReportRequestDto createHealthReportRequestDto);

  List<HealthReportMultiResponseDto> getHealthReport(Long userId);

  HealthReportResponseDto getHealthReportDetail(String id);

  HealthReportResponseDto updateHealthReport(String id, UpdateHealthReportRequestDto updateHealthReportRequestDto);

  String deleteHealthReport(String id, DeleteHealthReportRequestDto deleteHealthReportRequestDto);

}
