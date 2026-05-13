package com.example.contentservice.ocr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.contentservice.config.ClovaOcrClient;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Tag("integration")
@SpringBootTest
@ActiveProfiles("test")
class OcrServiceIntegrationTest {

  @Autowired
  private OcrService ocrService;

  @Autowired
  private OcrRepository ocrRepository;

  @MockitoBean
  private ClovaOcrClient clovaOcrClient;

  @BeforeEach
  void setUp() {
    ocrRepository.deleteAll();
  }

  @Test
  @DisplayName("통합 테스트: 이미지 파일을 분석하여 결과를 MongoDB에 저장해야 한다")
  void analyzeImage_Integration_Success_Test() throws IOException {
    // 1. Given: 테스트용 파일 및 OCR API 가짜 응답 데이터 준비
    Long userId = 100L;
    MockMultipartFile mockFile = new MockMultipartFile(
        "file",
        "test-receipt.jpg",
        "image/jpeg",
        "dummy-image-content".getBytes(StandardCharsets.UTF_8)
    );

    // Clova OCR API가 반환할 예상 JSON (OcrServiceImpl 로직에 맞춰 구성)
    // "진단서", "환자명 홍길동", "병명 감기", "2025년 12월 30일" 포함
    String mockOcrJson = """
        {
          "images": [
            {
              "fields": [
                {"inferText": "진단서", "inferConfidence": 0.99, "lineBreak": true},
                {"inferText": "환자명", "inferConfidence": 0.95, "lineBreak": false},
                {"inferText": "홍길동", "inferConfidence": 0.98, "lineBreak": true},
                {"inferText": "병명", "inferConfidence": 0.90, "lineBreak": false},
                {"inferText": "감기", "inferConfidence": 0.99, "lineBreak": true},
                {"inferText": "2025년", "inferConfidence": 0.99, "lineBreak": false},
                {"inferText": "12월", "inferConfidence": 0.99, "lineBreak": false},
                {"inferText": "30일", "inferConfidence": 0.99, "lineBreak": true}
              ]
            }
          ]
        }
        """;

    // ClovaClient가 호출되면 위 JSON을 반환하도록 설정
    when(clovaOcrClient.requestOcr(any(File.class))).thenReturn(mockOcrJson);

    // 2. When: 서비스 로직 실행
    OcrResponseDto responseDto = ocrService.analyzeImageWithClovaOcr(userId, mockFile);

    // 3. Then: 반환값 검증 및 DB 저장 확인

    // (1) 응답 DTO 검증
    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getPatientName()).isEqualTo("홍길동");
    assertThat(responseDto.getReportTitle()).contains("진단서");
    assertThat(responseDto.getDiagnosis()).contains("감기");
    assertThat(responseDto.getReportDate()).isEqualTo(LocalDate.of(2025, 12, 30));

    // (2) 실제 DB(Embedded Mongo) 저장 여부 검증
    List<OcrEntity> savedEntities = ocrRepository.findByUserId(userId);
    assertThat(savedEntities).hasSize(1);

    OcrEntity savedEntity = savedEntities.get(0);
    assertThat(savedEntity.getPatientName()).isEqualTo("홍길동");
    assertThat(savedEntity.getRawText()).isNotEmpty(); // 원문 데이터가 저장되었는지 확인
  }
}