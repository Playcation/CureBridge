package com.example.contentservice.scheduler;

import com.example.contentservice.ocr.service.OcrService;
import com.example.contentservice.report.service.HealthReportService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyReport {

  private final OcrService ocrService;
  private final HealthReportService healthReportService;

  /**
   * 매월 1일 오전 12시에 실행
   */
  @Scheduled(cron = "0 0 0 1 * *") // 초 분 시 일 월 요일
//  @Scheduled(cron = "0/10 * * * * *") // 초 분 시 일 월 요일
  public void updateRankingBatch() {
    log.info("레포트 작성 시작");
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();

    try {
      healthReportService.createHealthReportThisMonth(year, month);
      log.info("레포트 작성 완료");
    } catch (Exception e) {
      log.error("레포트 작성 중 오류 발생", e);
    }
  }

}
