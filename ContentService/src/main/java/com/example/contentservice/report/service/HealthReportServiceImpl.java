package com.example.contentservice.report.service;

import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.service.OcrService;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportMultiResponseDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class HealthReportServiceImpl implements HealthReportService {

  private final HealthReportRepository healthReportRepository;
  private final OcrService ocrService;

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.ai-model}")
  private String aiModel;

  @Override
  public HealthReportResponseDto createHealthReport(Long userId,
      CreateHealthReportRequestDto createHealthReportRequestDto) {
    int year = (createHealthReportRequestDto.getReportYear() != null) ? createHealthReportRequestDto.getReportYear() : LocalDateTime.now().getYear();
    int month = (createHealthReportRequestDto.getReportMonth() != null) ? createHealthReportRequestDto.getReportMonth() : LocalDateTime.now().getMonthValue();
    List<OcrEntity> ocrEntityList = ocrService.findOcrEntity(userId, year, month);
    if (ocrEntityList.isEmpty()){
      return null;
    }
    String prompt = buildPrompt(ocrEntityList, year, month);
    String result = callLLM(prompt);

    HealthReport healthReport = HealthReport.builder()
        .userId(userId)
        .title(year+"-"+month+"-건강 레포트 요약")
        .reportDate(year + "-" + month)
        .summary(result)
        .build();

    healthReportRepository.save(healthReport);

    System.out.println(result);
    return HealthReportResponseDto.toDto(healthReport);
  }

  @Override
  public List<HealthReportMultiResponseDto> getHealthReport(Long userId) {
    List<HealthReport> healthReportList = healthReportRepository.findByUserId(userId);
    return healthReportList.stream().map(HealthReportMultiResponseDto::toDto).toList();
  }

  @Override
  public HealthReportResponseDto getHealthReportDetail(String id) {
    HealthReport healthReport = healthReportRepository.findByIdOrElseThrow(id);
    return HealthReportResponseDto.toDto(healthReport);
  }

  @Override
  public HealthReportResponseDto updateHealthReport(String id,
      UpdateHealthReportRequestDto updateHealthReportRequestDto) {
    HealthReport healthReport = healthReportRepository.findByIdOrElseThrow(id);
    healthReport.updateHealthReport(updateHealthReportRequestDto);
    healthReportRepository.save(healthReport);
    return HealthReportResponseDto.toDto(healthReport);
  }

  @Override
  public String deleteHealthReport(String id,
      DeleteHealthReportRequestDto deleteHealthReportRequestDto) {
    healthReportRepository.deleteByIdOrElseThrow(id);
    return "삭제되었습니다.";
  }

  private String buildPrompt(List<OcrEntity> ocrEntities, int year, int month) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("당신은 건강 리포트 작성 전문가입니다.\n%d년 %d월 한 달간의 진단 내역은 다음과 같습니다:\n\n", year, month));

    int index = 1;
    for (OcrEntity entity : ocrEntities) {
      sb.append(String.format("%d. [%s] 제목: %s\n", index++, entity.getReportDate(), entity.getReportTitle()));
      sb.append(String.format("   병명: %s\n", entity.getDiagnosis()));

      // OCR 원문 일부만 요약
      if (entity.getRawText() != null && !entity.getRawText().isEmpty()) {
        sb.append(String.format("   OCR 내용: %s\n\n", String.join(" ", entity.getRawText())));
      }
    }

    sb.append("위 내용을 바탕으로 다음 조건에 맞는 건강 리포트를 작성해 주세요:\n");
    sb.append("- 사용자에게 친근한 말투\n");
    sb.append("- 주요 질병 요약\n");
    sb.append("- 생활습관/예방 팁 포함\n");
    sb.append("- 500자 내외\n");

    return sb.toString();
  }

  private String callLLM(String prompt) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(apiKey);

    Map<String, Object> body = new HashMap<>();
    body.put("model", aiModel);
    body.put("messages", List.of(
        Map.of("role", "system", "content", "너는 건강 관리 코치야."),
        Map.of("role", "user", "content", prompt)
    ));

    HttpEntity<?> request = new HttpEntity<>(body, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(
        "https://api.openai.com/v1/chat/completions", request, Map.class
    );

    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
    return (String) ((Map) choices.get(0).get("message")).get("content");
  }
}
