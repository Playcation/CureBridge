package com.example.contentservice.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Tag("integration")
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MonthlyReportIntegrationTest {

  @Autowired
  private MonthlyReport monthlyReport;

  @Autowired
  private OcrRepository ocrRepository;

  @Autowired
  private HealthReportRepository healthReportRepository;

  @BeforeEach
  void setUp() {
    ocrRepository.deleteAll();
    healthReportRepository.deleteAll();
  }

  @Test
  @Transactional
  @DisplayName("이번 달 OCR 데이터가 있을 때 리포트가 생성되어야 한다")
  void monthlyReport_Integration_Success_Test() {
    // 1. Given: 현재 날짜 기준의 테스트 데이터 DB 저장
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();
    Long userId = 1L;

    OcrEntity ocrData = OcrEntity.builder()
        .userId(userId)
        .ocrId("ocr-test-id-1")
        .reportTitle("통합 테스트용 진단서")
        .diagnosis("테스트 병명")
        .reportDate(LocalDate.now()) // 이번 달 데이터여야 함
        .rawText(List.of("증상 내용", "처방 내용"))
        .build();

    ocrRepository.save(ocrData);

    // LLM 응답 Mocking 준비
    Map<String, Object> mockMessage = Map.of("content", "건강 리포트 요약 내용입니다. 별점 5");
    Map<String, Object> mockChoice = Map.of("message", mockMessage);
    Map<String, Object> mockBody = Map.of("choices", List.of(mockChoice));
    ResponseEntity<Map> mockResponse = new ResponseEntity<>(mockBody, HttpStatus.OK);

    // 2. When: 스케줄러 실행 (RestTemplate Mocking 포함)
    // 서비스 코드 내부에서 new RestTemplate()을 사용하므로 mockConstruction 필요
    try (MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
        (mock, context) -> {
          when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
              .thenReturn(mockResponse);
        })) {

      monthlyReport.updateRankingBatch();
    }

    // 3. Then: DB에 리포트가 저장되었는지 검증
    List<HealthReport> reports = healthReportRepository.findByUserId(userId);

    for (int i = 0; i < reports.size(); i++) {
      System.out.println("start!!!!!!!!!!!!!!!!!");
      System.out.println("Title " +
          reports.get(i).getTitle() + " Summary " + reports.get(i).getSummary() + " Rate "
          + reports.get(i)
          .getRate());
      System.out.println("end!!!!!!!!!!!!!!!!!");
    }

    assertThat(reports).hasSize(1);
    HealthReport savedReport = reports.get(0);
    assertThat(savedReport.getTitle()).contains(year + "-" + month);
    assertThat(savedReport.getSummary()).contains("건강 리포트 요약 내용");
    assertThat(savedReport.getRate()).isEqualTo(5);
  }

  @Test
  @Transactional
  @DisplayName("이번 달 OCR 데이터가 없으면 리포트가 생성되지 않아야 한다")
  void monthlyReport_NoData_Test() {
    // 1. Given: 데이터 없음 (저장 안 함)
    // 확실한 검증을 위해 다른 달의 데이터를 넣을 수도 있음
    OcrEntity pastData = OcrEntity.builder()
        .userId(2L)
        .ocrId("ocr-past")
        .reportDate(LocalDate.now().minusMonths(2)) // 2달 전 데이터
        .build();
    ocrRepository.save(pastData);

    // 2. When: 스케줄러 실행
    monthlyReport.updateRankingBatch();

    // 3. Then: 리포트가 생성되지 않아야 함
    List<HealthReport> reports = healthReportRepository.findAll();
    // 테스트 환경이므로 기존 데이터가 0건이라고 가정하거나, 사이즈 변화를 체크
    assertThat(reports).isEmpty();
  }
}