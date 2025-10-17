package com.example.contentservice.ocr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import com.example.contentservice.report.service.HealthReportServiceImpl;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OcrServiceImplTest {

  @Mock
  private HealthReportRepository healthReportRepository;

  @Mock
  private OcrService ocrService;

  @InjectMocks
  private HealthReportServiceImpl healthReportService;

  private HealthReport report;
  private OcrEntity ocrEntity;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(healthReportService, "apiKey", "dummy-key");
    ReflectionTestUtils.setField(healthReportService, "aiModel", "gpt-4o-mini");

    report = HealthReport.builder()
        .id("report1")
        .userId(1L)
        .title("2025-09-건강 레포트")
        .reportDate(LocalDate.of(2025, 9, 1))
        .summary("요약내용")
        .rate(4)
        .build();

    ocrEntity = OcrEntity.builder()
        .ocrId("100L")
        .userId(1L)
        .reportDate(LocalDate.of(2025, 9, 1))
        .reportTitle("진단서")
        .diagnosis("감기")
        .rawText(List.of("내용1", "내용2"))
        .build();
  }

  @Test
  @DisplayName("성공: 건강 리포트 생성")
  void createHealthReport_success() {
    when(ocrService.findOcrEntity(1L, 2025, 9)).thenReturn(List.of(ocrEntity));

    HealthReportServiceImpl spyService = spy(healthReportService);
    doReturn("리포트 요약\n별점 4").when(spyService).callLLM(anyString());
    when(healthReportRepository.save(any(HealthReport.class))).thenReturn(report);

    CreateHealthReportRequestDto req = new CreateHealthReportRequestDto(2025, 9);
    HealthReportResponseDto response = spyService.createHealthReport(1L, req);

    assertNotNull(response);
    assertEquals(1L, response.getUserId());
    assertEquals(4, response.getRate());
  }

  @Test
  @DisplayName("실패: OCR 데이터 없으면 리포트 생성 실패")
  void createHealthReport_fail_noOcr() {
    when(ocrService.findOcrEntity(1L, 2025, 9)).thenReturn(List.of());

    CreateHealthReportRequestDto req = new CreateHealthReportRequestDto(2025, 9);
    HealthReportResponseDto response = healthReportService.createHealthReport(1L, req);

    assertNull(response);
  }

  @Test
  @DisplayName("성공: 리포트 상세 조회")
  void getHealthReportDetail_success() {
    when(healthReportRepository.findByIdOrElseThrow("report1")).thenReturn(report);
    when(ocrService.findOcrEntity(1L, 2025, 9)).thenReturn(List.of(ocrEntity));

    HealthReportResponseDto response = healthReportService.getHealthReportDetail("report1");

    assertEquals("감기", response.getOcrResults().get(0).getDiagnosis());
  }

  @Test
  @DisplayName("실패: 없는 ID 조회 시 예외 발생")
  void getHealthReportDetail_fail_invalidId() {
    when(healthReportRepository.findByIdOrElseThrow("wrongId"))
        .thenThrow(new IllegalArgumentException("Report not found"));

    assertThrows(IllegalArgumentException.class,
        () -> healthReportService.getHealthReportDetail("wrongId"));
  }

  @Test
  @DisplayName("성공: 리포트 수정")
  void updateHealthReport_success() {
    UpdateHealthReportRequestDto dto = new UpdateHealthReportRequestDto("새로운 요약", 5);

    when(healthReportRepository.findByIdOrElseThrow("report1")).thenReturn(report);
    when(healthReportRepository.save(any(HealthReport.class))).thenReturn(report);
    when(ocrService.findOcrEntity(1L, 2025, 9)).thenReturn(List.of(ocrEntity));

    HealthReportResponseDto result = healthReportService.updateHealthReport("report1", dto);

    assertEquals("새로운 요약", result.getSummary());
    assertEquals(5, result.getRate());
  }

  @Test
  @DisplayName("성공: 리포트 삭제")
  void deleteHealthReport_success() {
    doNothing().when(healthReportRepository).deleteByIdOrElseThrow("report1");

    String result = healthReportService.deleteHealthReport("report1",
        new DeleteHealthReportRequestDto("pw"));

    assertEquals("삭제되었습니다.", result);
  }
}