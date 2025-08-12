package com.example.contentservice.ocr.service;

import com.example.contentservice.config.ClovaOcrClient;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    void analyzeImageWithClovaOcr() throws IOException {
        // Given
        Long userId = 1L;
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        String ocrResultJson = "{\"images\":[{\"fields\":[{\"inferText\":\"진단서\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"환자명 홍길동\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"병명 감기\",\"inferConfidence\":0.9,\"lineBreak\":true},{\"inferText\":\"2023년 1월 1일\",\"inferConfidence\":0.9,\"lineBreak\":true}]}]}";

        when(clovaOcrClient.requestOcr(any())).thenReturn(ocrResultJson);
        when(ocrRepository.insert(any(OcrEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        OcrResponseDto responseDto = ocrService.analyzeImageWithClovaOcr(userId, mockFile);

        // Then
        assertEquals("진단서", responseDto.getReportTitle());
        assertEquals("홍길동", responseDto.getPatientName());
        assertEquals("감기", responseDto.getDiagnosis());
        assertEquals(LocalDate.of(2023, 1, 1), responseDto.getReportDate());
    }

    @Test
    @DisplayName("OCR 결과 업데이트 테스트")
    void updateOcrResult() {
        UpdateRequestDto updateRequestDto = new UpdateRequestDto("수정된 진단서", LocalDate.now().plusDays(1), "김철수", "독감", Arrays.asList("new text1", "new text2"));
        when(ocrRepository.findByIdOrElseThrow("testId")).thenReturn(ocrEntity);
        when(ocrRepository.save(any(OcrEntity.class))).thenReturn(ocrEntity);

        OcrResponseDto ocrResponseDto = ocrService.updateOcrResult("testId", updateRequestDto);

        assertEquals("수정된 진단서", ocrResponseDto.getReportTitle());
        assertEquals("김철수", ocrResponseDto.getPatientName());
    }

    @Test
    @DisplayName("OCR 상세 결과 조회 테스트")
    void getOcrDetailResult() {
        when(ocrRepository.findByIdOrElseThrow("testId")).thenReturn(ocrEntity);

        OcrResponseDto ocrResponseDto = ocrService.getOcrDetalResult("testId");

        assertEquals("진단서", ocrResponseDto.getReportTitle());
        assertEquals("홍길동", ocrResponseDto.getPatientName());
    }

    @Test
    @DisplayName("사용자 OCR 결과 목록 조회 테스트")
    void getOcrResult() {
        when(ocrRepository.findByUserId(1L)).thenReturn(Arrays.asList(ocrEntity));

        var result = ocrService.getOcrResult(1L);

        assertEquals(1, result.size());
        assertEquals("진단서", result.get(0).getReportTitle());
    }

    @Test
    @DisplayName("OCR 결과 삭제 테스트")
    void deleteOcrResult() {
        String result = ocrService.deleteOcrResult("testId", new com.example.contentservice.ocr.dto.DeleteRequestDto("password"));

        assertEquals("삭제되었습니다.", result);
    }
}
