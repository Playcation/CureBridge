package com.example.contentservice.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.service.OcrService;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class HealthReportServiceImplTest {

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

    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();
    int day = LocalDate.now().getDayOfMonth();

    report = HealthReport.builder()
        // 서비스 코드에서 Long.valueOf(id)를 수행하므로 숫자형 문자열로 ID 설정
        .id("100")
        .userId(1L)
        .title(year + "-" + month + "-건강 레포트")
        .reportDate(LocalDate.of(year, month, day))
        .summary("요약내용")
        .rate(4)
        .build();

    ocrEntity = OcrEntity.builder()
        .ocrId("ocr1")
        .userId(1L)
        .reportDate(LocalDate.now())
        .reportTitle("진단서")
        .diagnosis("감기")
        .rawText(List.of("내용1", "내용2"))
        .build();
  }

  @Test
  @DisplayName("건강 리포트 생성 - OCR 데이터 없음")
  void createHealthReport_NoOcrData_Test() {
    // Given
    CreateHealthReportRequestDto requestDto = new CreateHealthReportRequestDto(2023, 5);
    when(ocrService.findOcrEntity(anyLong(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

    // When
    HealthReportResponseDto result = healthReportService.createHealthReport(1L, requestDto);

    // Then
    assertNull(result);
  }

  @Test
  @DisplayName("건강 리포트 생성 - 성공 (RestTemplate Mocking)")
  void createHealthReport_Success_Test() {
    // Given
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();
    CreateHealthReportRequestDto requestDto = new CreateHealthReportRequestDto(year, month);

    // OCR 데이터가 있다고 가정
    when(ocrService.findOcrEntity(1L, year, month)).thenReturn(List.of(ocrEntity));
    when(healthReportRepository.save(any(HealthReport.class))).thenAnswer(i -> i.getArguments()[0]);

    // OpenAI 응답 Mocking 구조 생성
    Map<String, Object> messageMap = Map.of("content", "건강 리포트 내용입니다. 별점 5");
    Map<String, Object> choiceMap = Map.of("message", messageMap);
    Map<String, Object> bodyMap = Map.of("choices", List.of(choiceMap));
    ResponseEntity<Map> mockResponse = new ResponseEntity<>(bodyMap, HttpStatus.OK);

    // try-with-resources 구문을 사용하여 RestTemplate 생성자 Mocking
    try (MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
        (mock, context) -> {
          when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
              .thenReturn(mockResponse);
        })) {

      // When
      HealthReportResponseDto result = healthReportService.createHealthReport(1L, requestDto);

      // Then
      assertNotNull(result);
      assertEquals(year + "-" + month + "-건강 레포트 요약", result.getTitle());
      assertEquals(5, result.getRate()); // LLM 응답에서 파싱한 별점
      verify(healthReportRepository).save(any(HealthReport.class));
    }
  }

  @Test
  @DisplayName("건강 리포트 생성 실패 - 외부 API(LLM) 호출 에러")
  void createHealthReport_LlmError_Test() {
    // Given
    CreateHealthReportRequestDto requestDto = new CreateHealthReportRequestDto(2023, 5);

    // OCR 데이터는 존재한다고 가정
    when(ocrService.findOcrEntity(anyLong(), anyInt(), anyInt())).thenReturn(List.of(ocrEntity));

    // RestTemplate이 외부 API 호출 시 예외를 던지도록 설정
    try (MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
        (mock, context) -> {
          when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
              .thenThrow(new org.springframework.web.client.RestClientException("OpenAI Server Error"));
        })) {

      // When & Then
      // 서비스 코드 내에서 try-catch로 잡지 않으므로 예외가 전파되어야 함
      assertThrows(org.springframework.web.client.RestClientException.class, () ->
          healthReportService.createHealthReport(1L, requestDto)
      );
    }
  }


  @Test
  @DisplayName("건강 리포트 목록 조회 - 날짜 필터링 확인")
  void getHealthReport_Test() {
    // Given
    // 리포트 날짜: 오늘
    when(healthReportRepository.findByUserId(1L)).thenReturn(List.of(report));

    // OCR 결과: 하나는 오늘 날짜(매칭), 하나는 다른 날짜(매칭X)
    OcrResponseDto matchingOcr = OcrResponseDto.builder()
        .reportDate(report.getReportDate()) // 같은 날짜
        .build();
    OcrResponseDto nonMatchingOcr = OcrResponseDto.builder()
        .reportDate(report.getReportDate().minusMonths(1)) // 다른 달
        .build();

    when(ocrService.getOcrResult(1L)).thenReturn(List.of(matchingOcr, nonMatchingOcr));

    // When
    List<HealthReportResponseDto> results = healthReportService.getHealthReport(1L);

    // Then
    assertEquals(1, results.size());
    assertEquals(report.getReportDate(), results.get(0).getReportDate());
  }

  @Test
  @DisplayName("건강 리포트 상세 조회")
  void getHealthReportDetail_Test() {
    // Given
    String reportId = "100";
    when(healthReportRepository.findByIdOrElseThrow(reportId)).thenReturn(report);
    // 서비스 코드에서 Long.valueOf(reportId)를 사용해 OCR 검색
    when(ocrService.findOcrEntity(100L, report.getReportDate().getYear(), report.getReportDate().getMonthValue()))
        .thenReturn(List.of(ocrEntity));

    // When
    HealthReportResponseDto result = healthReportService.getHealthReportDetail(reportId);

    // Then
    assertNotNull(result);
    assertEquals(report.getTitle(), result.getTitle());
  }

  @Test
  @DisplayName("건강 리포트 상세 조회 실패 - 리포트 ID 형식이 숫자가 아님 (NumberFormatException)")
  void getHealthReportDetail_InvalidIdFormat_Test() {
    // Given
    String nonNumericId = "report-uuid-abc";

    when(healthReportRepository.findByIdOrElseThrow(nonNumericId)).thenReturn(report);

    // When & Then
    assertThrows(NumberFormatException.class, () ->
        healthReportService.getHealthReportDetail(nonNumericId)
    );
  }

  @Test
  @DisplayName("건강 리포트 수정")
  void updateHealthReport_Test() {
    // Given
    String reportId = "100";
    UpdateHealthReportRequestDto updateDto = mock(UpdateHealthReportRequestDto.class);
    when(updateDto.getTitle()).thenReturn("수정된 제목");
    when(updateDto.getSummary()).thenReturn("수정된 요약");
    when(updateDto.getRate()).thenReturn(3);
    when(updateDto.getReportDate()).thenReturn(LocalDate.now());

    when(healthReportRepository.findByIdOrElseThrow(reportId)).thenReturn(report);

    // 수정 후 OCR 재조회 로직
    when(ocrService.findOcrEntity(anyLong(), anyInt(), anyInt())).thenReturn(List.of(ocrEntity));

    // When
    HealthReportResponseDto result = healthReportService.updateHealthReport(reportId, updateDto);

    // Then
    assertEquals("수정된 제목", result.getTitle());
    assertEquals(3, result.getRate());
    verify(healthReportRepository).save(report);
  }

  @Test
  @DisplayName("건강 리포트 수정 실패 - 존재하지 않는 리포트 ID")
  void updateHealthReport_NotFound_Test() {
    // Given
    String invalidId = "999";
    UpdateHealthReportRequestDto updateDto = new UpdateHealthReportRequestDto();

    // Repository가 예외를 던지도록 설정 (예: NoSuchElementException)
    // 실제 프로젝트의 findByIdOrElseThrow 구현에 맞춰 예외 클래스를 수정하세요 (여기선 RuntimeException 가정)
    when(healthReportRepository.findByIdOrElseThrow(invalidId))
        .thenThrow(new RuntimeException("해당 리포트를 찾을 수 없습니다."));

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        healthReportService.updateHealthReport(invalidId, updateDto)
    );
    assertEquals("해당 리포트를 찾을 수 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("건강 리포트 삭제")
  void deleteHealthReport_Test() {
    // Given
    String reportId = "100";
    DeleteHealthReportRequestDto deleteDto = new DeleteHealthReportRequestDto();

    // When
    String result = healthReportService.deleteHealthReport(reportId, deleteDto);

    // Then
    assertEquals("삭제되었습니다.", result);
    verify(healthReportRepository).deleteByIdOrElseThrow(reportId);
  }

  @Test
  @DisplayName("건강 리포트 삭제 실패 - 존재하지 않는 ID")
  void deleteHealthReport_NotFound_Test() {
    // Given
    String invalidId = "unknown";
    DeleteHealthReportRequestDto deleteDto = new DeleteHealthReportRequestDto();

    // deleteByIdOrElseThrow가 실패 시 예외를 던진다고 가정
    // Mockito의 doThrow 사용 (void 메서드인 경우)
    // 실제 구현에 따라 예외 클래스를 맞춰주세요.
    org.mockito.Mockito.doThrow(new RuntimeException("삭제할 리포트가 없습니다."))
        .when(healthReportRepository).deleteByIdOrElseThrow(invalidId);

    // When & Then
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        healthReportService.deleteHealthReport(invalidId, deleteDto)
    );
    assertEquals("삭제할 리포트가 없습니다.", exception.getMessage());
  }

  @Test
  @DisplayName("이번 달 리포트 생성 (스케줄러) - 다중 사용자 처리")
  void createHealthReportThisMonth_Test() {
    // Given
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();

    // User 1, User 2의 OCR 데이터 준비
    OcrEntity user1Ocr = OcrEntity.builder().userId(1L).rawText(List.of("내용1")).build();
    OcrEntity user2Ocr = OcrEntity.builder().userId(2L).rawText(List.of("내용2")).build();

    when(ocrService.findOcrEntityThisMonth(year, month)).thenReturn(List.of(user1Ocr, user2Ocr));

    // OpenAI 응답 Mock
    Map<String, Object> messageMap = Map.of("content", "내용... 별점 4");
    Map<String, Object> choiceMap = Map.of("message", messageMap);
    Map<String, Object> bodyMap = Map.of("choices", List.of(choiceMap));
    ResponseEntity<Map> mockResponse = new ResponseEntity<>(bodyMap, HttpStatus.OK);

    // RestTemplate Mocking
    try (MockedConstruction<RestTemplate> mockedRestTemplate = mockConstruction(RestTemplate.class,
        (mock, context) -> {
          when(mock.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
              .thenReturn(mockResponse);
        })) {

      // When
      String result = healthReportService.createHealthReportThisMonth(year, month);

      // Then
      assertEquals("스케줄링 완성", result);
      // 사용자가 2명이므로 save가 2번 호출되어야 함
      verify(healthReportRepository, times(2)).save(any(HealthReport.class));
    }
  }
}