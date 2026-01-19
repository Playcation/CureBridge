package com.example.contentservice.scheduler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.contentservice.ocr.service.OcrService;
import com.example.contentservice.report.service.HealthReportService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MonthlyReportTest {

  @Mock
  private HealthReportService healthReportService;

  @Mock
  private OcrService ocrService;

  @InjectMocks
  private MonthlyReport monthlyReport;

  @Test
  @DisplayName("스케줄러 정상 실행 테스트 - 서비스 메소드 호출 확인")
  void updateRankingBatch_Success_Test() {
    // Given & When
    monthlyReport.updateRankingBatch();

    // Then
    verify(healthReportService, times(1)).createHealthReportThisMonth(anyInt(), anyInt());
  }

  @Test
  @DisplayName("스케줄러 예외 발생 시 처리 테스트 - 예외를 잡아서 로그를 찍고 종료")
  void updateRankingBatch_Exception_Test() {
    // Given
    doThrow(new RuntimeException("스케줄링 에러 발생"))
        .when(healthReportService).createHealthReportThisMonth(anyInt(), anyInt());

    // When & Then
    assertDoesNotThrow(() -> monthlyReport.updateRankingBatch());
    verify(healthReportService, times(1)).createHealthReportThisMonth(anyInt(), anyInt());
  }

  @Test
  @DisplayName("날짜 테스트")
  void updateRankingBatch_DateCheck_Test() {
    LocalDate fixedDate = LocalDate.of(2025, 12, 1);

    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
      mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

      monthlyReport.updateRankingBatch();

      // 2025년 12월이 정확히 넘어갔는지 확인
      verify(healthReportService).createHealthReportThisMonth(2025, 12);
    }
  }
}