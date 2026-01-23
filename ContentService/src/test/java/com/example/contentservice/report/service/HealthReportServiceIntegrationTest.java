package com.example.contentservice.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import com.example.contentservice.config.ClovaOcrClient;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(properties = {
    "openai.api-key=test-dummy-key",
    "openai.ai-model=gpt-4o-mini"
})
@ActiveProfiles("test")
class HealthReportServiceIntegrationTest {

  @Autowired
  private HealthReportService healthReportService;

  @Autowired
  private HealthReportRepository healthReportRepository;

  @Autowired
  private OcrRepository ocrRepository;

  // OcrService가 의존하는 외부 클라이언트는 Mock 처리하여 컨텍스트 로딩 에러 방지
  @MockitoBean
  private ClovaOcrClient clovaOcrClient;

  @BeforeEach
  void setUp() {
    healthReportRepository.deleteAll();
    ocrRepository.deleteAll();
  }

  @Test
  @DisplayName("통합 테스트: DB에 저장된 OCR 데이터를 조회하여 리포트를 생성하고 저장해야 한다")
  void createHealthReport_Integration_Success_Test() {
    // 1. Given: 테스트 데이터 준비
    Long userId = 1L;
    int year = 2025;
    int month = 5;

    // (1) OCR 데이터 미리 저장 (DB 연동 확인)
    // OcrService.findOcrEntity()가 DB에서 이 데이터를 찾아옵니다.
    OcrEntity ocrEntity = OcrEntity.builder()
        .userId(userId)
        .reportDate(LocalDate.of(year, month, 15))
        .reportTitle("내과 진단서")
        .diagnosis("감기")
        .rawText(List.of("기침", "발열", "약 처방"))
        .build();
    ocrRepository.save(ocrEntity);

    CreateHealthReportRequestDto requestDto = new CreateHealthReportRequestDto(year, month);

    // (2) OpenAI API Mock 응답 준비
    // 서비스 로직이 파싱할 "별점 4" 텍스트 포함
    Map<String, Object> messageMap = Map.of("content", "건강 상태 요약: 감기 증상이 있습니다. 충분한 휴식이 필요합니다. 별점 4");
    Map<String, Object> choiceMap = Map.of("message", messageMap);
    Map<String, Object> bodyMap = Map.of("choices", List.of(choiceMap));
    ResponseEntity<Map> mockResponse = new ResponseEntity<>(bodyMap, HttpStatus.OK);

    // 2. When: RestTemplate 생성자 가로채기 및 서비스 호출
    // 서비스 코드 내부의 'new RestTemplate()' 호출을 가로채서 Mock 객체를 반환하도록 설정
    try (MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
        (mock, context) -> {
          when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
              .thenReturn(mockResponse);
        })) {

      HealthReportResponseDto responseDto = healthReportService.createHealthReport(userId, requestDto);

      // 3. Then: 결과 검증

      // (1) 응답 DTO 검증
      assertThat(responseDto).isNotNull();
      assertThat(responseDto.getTitle()).isEqualTo("2025-5-건강 레포트 요약");
      assertThat(responseDto.getSummery()).contains("감기 증상");
      assertThat(responseDto.getRate()).isEqualTo(4); // "별점 4" 파싱 결과 확인

      // (2) DB 저장 여부 확인
      List<HealthReport> savedReports = healthReportRepository.findByUserId(userId);
      assertThat(savedReports).hasSize(1);

      HealthReport savedReport = savedReports.get(0);
      assertThat(savedReport.getSummary()).isEqualTo("건강 상태 요약: 감기 증상이 있습니다. 충분한 휴식이 필요합니다. 별점 4");
      assertThat(savedReport.getRate()).isEqualTo(4);
    }
  }
}