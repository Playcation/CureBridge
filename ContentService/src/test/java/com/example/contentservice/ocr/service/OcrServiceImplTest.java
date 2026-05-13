package com.example.contentservice.ocr.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.contentservice.config.ClovaOcrClient;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;


@ExtendWith(MockitoExtension.class)
class OcrServiceImplTest {

  @Mock
  private OcrRepository ocrRepository;
  @Mock
  private ClovaOcrClient clovaOcrClient;

  @InjectMocks
  private OcrServiceImpl ocrService;

  private OcrEntity ocrEntity;

  @BeforeEach
  void setUp() {
    ocrEntity = OcrEntity.builder()
        .ocrId("testId")
        .userId(1L)
        .reportTitle("진단서")
        .reportDate(LocalDate.now())
        .patientName("홍길동")
        .diagnosis("감기")
        .rawText(Arrays.asList("text1", "text2"))
        .build();
  }

  @Test
  @DisplayName("OCR 파일 업로드 및 분석 테스트")
  void analyzeImageWithClovaOcrTest() throws IOException {
    // Given
    Long userId = 1L;
    MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
        "test data".getBytes());
    String ocrResultJson = "{\"images\":[{\"fields\":[{\"inferText\":\"진단서\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"환자명 홍길동\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"병명 감기\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"2023년 1월 1일\",\"inferConfidence\":0.9,\"lineBreak\":true}]}]}";

    when(clovaOcrClient.requestOcr(any())).thenReturn(ocrResultJson);
    when(ocrRepository.insert(any(OcrEntity.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    // When
    OcrResponseDto responseDto = ocrService.analyzeImageWithClovaOcr(userId, mockFile);

    // Then
    assertEquals("진단서", responseDto.getReportTitle());
    assertEquals("홍길동", responseDto.getPatientName());
    assertEquals("감기", responseDto.getDiagnosis());
    assertEquals(LocalDate.of(2023, 1, 1), responseDto.getReportDate());
  }

  @Test
  @DisplayName("OCR 분석 실패 - 파일 처리 중 IOException 발생")
  void analyzeImageWithClovaOcr_FileError_Test() throws IOException {
    // Given
    Long userId = 1L;
    // transferTo 호출 시 IOException이 발생하도록 Mock 설정
    MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
        "test data".getBytes()) {
      @Override
      public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        throw new IOException("Disk full or permission denied");
      }
    };

    // When & Then
    // Service 내부에서 try-catch로 잡아 RuntimeException으로 던지는지 확인
    org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
      ocrService.analyzeImageWithClovaOcr(userId, mockFile);
    });
  }

  @Test
  @DisplayName("OCR 분석 실패 - 외부 API 응답 오류 (JSON 파싱 실패)")
  void analyzeImageWithClovaOcr_JsonParseError_Test() throws IOException {
    // Given
    Long userId = 1L;
    MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg",
        "test data".getBytes());

    // 정상적인 JSON이 아닌 에러 메시지나 잘못된 형식이 반환된 경우
    String invalidJson = "Internal Server Error";

    when(clovaOcrClient.requestOcr(any())).thenReturn(invalidJson);

    // When & Then
    // JSONObject 파싱 중 에러가 발생하여 RuntimeException으로 전파되는지 확인
    org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
      ocrService.analyzeImageWithClovaOcr(userId, mockFile);
    });
  }

  @Test
  @DisplayName("OCR 결과 업데이트 테스트")
  void updateOcrResultTest() {
    // Given
    UpdateRequestDto updateRequestDto = new UpdateRequestDto("수정된 진단서", LocalDate.now().plusDays(1),
        "김철수", "독감", Arrays.asList("new text1", "new text2"));
    when(ocrRepository.findByIdOrElseThrow("testId")).thenReturn(ocrEntity);
    when(ocrRepository.save(any(OcrEntity.class))).thenReturn(ocrEntity);

    // When
    OcrResponseDto ocrResponseDto = ocrService.updateOcrResult("testId", updateRequestDto);

    // Then
    assertEquals("수정된 진단서", ocrResponseDto.getReportTitle());
    assertEquals("김철수", ocrResponseDto.getPatientName());
  }

  @Test
  @DisplayName("OCR 업데이트 실패 - 존재하지 않는 ID 조회")
  void updateOcrResult_NotFound_Test() {
    // Given
    String invalidId = "unknownId";
    UpdateRequestDto updateRequestDto = new UpdateRequestDto("title", LocalDate.now(), "name",
        "diag", List.of());

    // findByIdOrElseThrow가 예외를 던지도록 설정 (Repository 구현체에 따라 예외 종류는 다를 수 있음)
    // 여기서는 보통 사용되는 NoSuchElementException 또는 IllegalArgumentException 가정
    when(ocrRepository.findByIdOrElseThrow(invalidId))
        .thenThrow(new java.util.NoSuchElementException("해당 ID를 찾을 수 없습니다."));

    // When & Then
    org.junit.jupiter.api.Assertions.assertThrows(java.util.NoSuchElementException.class, () -> {
      ocrService.updateOcrResult(invalidId, updateRequestDto);
    });
  }

  @Test
  @DisplayName("OCR 상세 결과 조회 테스트")
  void getOcrDetailResultTest() {
    // Given
    when(ocrRepository.findByIdOrElseThrow("testId")).thenReturn(ocrEntity);

    // When
    OcrResponseDto ocrResponseDto = ocrService.getOcrDetalResult("testId");

    // Then
    assertEquals("진단서", ocrResponseDto.getReportTitle());
    assertEquals("홍길동", ocrResponseDto.getPatientName());
  }

  @Test
  @DisplayName("사용자 OCR 결과 목록 조회 테스트")
  void getOcrResultTest() {
    // Given
    when(ocrRepository.findByUserId(1L)).thenReturn(Arrays.asList(ocrEntity));

    // When
    var result = ocrService.getOcrResult(1L);

    // Then
    assertEquals(1, result.size());
    assertEquals("진단서", result.get(0).getReportTitle());
  }

  @Test
  @DisplayName("OCR 결과 삭제 테스트")
  void deleteOcrResultTest() {
    // Given & When
    String result = ocrService.deleteOcrResult("testId",
        new com.example.contentservice.ocr.dto.DeleteRequestDto("password"));

    // Then
    assertEquals("삭제되었습니다.", result);
  }

  @Test
  @DisplayName("userId와 날짜(년, 월)로 OCR 검색 테스트")
  void findOcrEntityTest() {
    // Given
    Long userId = 1L;
    // Given
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();

    // 서비스 로직 내부에서 계산될 것으로 예상되는 날짜
    LocalDate expectedStart = LocalDate.of(year, month, 1);
    LocalDate expectedEnd = LocalDate.of(year, month, 31);
    when(ocrRepository.findByUserIdAndReportDateBetween(userId, expectedStart, expectedEnd))
        .thenReturn(Arrays.asList(ocrEntity));

    // When
    List<OcrEntity> result = ocrService.findOcrEntity(userId, year, month);

    // Then
    assertEquals(1, result.size());
    assertEquals(ocrEntity.getOcrId(), result.get(0).getOcrId());
    assertEquals(ocrEntity.getPatientName(), result.get(0).getPatientName());
    assertEquals(ocrEntity.getCreatedAt(), result.get(0).getCreatedAt());
  }

  @Test
  @DisplayName("날짜(년, 월)로 전체 OCR 검색 테스트")
  void findOcrEntityThisMonthTest() {
    // Given
    int year = LocalDate.now().getYear();
    int month = LocalDate.now().getMonthValue();

    // 서비스 로직 내부에서 계산될 것으로 예상되는 날짜
    LocalDate expectedStart = LocalDate.of(year, month, 1);
    LocalDate expectedEnd = LocalDate.of(year, month, 31);
    when(ocrRepository.findByReportDateBetween(expectedStart, expectedEnd))
        .thenReturn(Arrays.asList(ocrEntity));

    // When
    List<OcrEntity> result = ocrService.findOcrEntityThisMonth(year, month);

    // Then
    assertEquals(1, result.size());
    assertEquals(ocrEntity.getReportTitle(), result.get(0).getReportTitle());
    assertEquals(ocrEntity.getCreatedAt(), result.get(0).getCreatedAt());
  }

  @Test
  @DisplayName("OCR 검색 실패 - 유효하지 않는 날짜 입력 (DateTimeException)")
  void findOcrEntity_InvalidDate_Test() {
    // Given
    Long userId = 1L;
    int year = 2025;
    int invalidMonth = 13; // 13월은 존재하지 않음

    // When & Then
    // LocalDate.of(year, month, 1) 에서 DateTimeException 발생 예상
    org.junit.jupiter.api.Assertions.assertThrows(java.time.DateTimeException.class, () -> {
      ocrService.findOcrEntity(userId, year, invalidMonth);
    });
  }
}
