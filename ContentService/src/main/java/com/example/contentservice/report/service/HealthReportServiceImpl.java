package com.example.contentservice.report.service;

import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.service.OcrService;
import com.example.contentservice.report.dto.CreateHealthReportRequestDto;
import com.example.contentservice.report.dto.DeleteHealthReportRequestDto;
import com.example.contentservice.report.dto.HealthReportListResponseDto;
import com.example.contentservice.report.dto.HealthReportResponseDto;
import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import com.example.contentservice.report.entity.HealthReport;
import com.example.contentservice.report.repository.HealthReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthReportServiceImpl implements HealthReportService {

  private final HealthReportRepository healthReportRepository;
  private final OcrService ocrService;
  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${openai.api-key}")
  private String apiKey;

  @Value("${openai.ai-model}")
  private String aiModel;

  @Override
  @CacheEvict(value = "healthReport", key = "#userId", cacheManager = "healthReportCacheManager")
  public HealthReportResponseDto createHealthReport(Long userId,
      CreateHealthReportRequestDto createHealthReportRequestDto) {

    int year = (createHealthReportRequestDto.getReportYear() != null)
        ? createHealthReportRequestDto.getReportYear() : LocalDateTime.now().getYear();
    int month = (createHealthReportRequestDto.getReportMonth() != null)
        ? createHealthReportRequestDto.getReportMonth() : LocalDateTime.now().getMonthValue();
    List<OcrEntity> ocrEntityList = ocrService.findOcrEntity(userId, year, month);
    if (ocrEntityList.isEmpty()) {
      return null;
    }
    String prompt = buildPrompt(ocrEntityList, year, month);
    String result = callLLM(prompt);

    Pattern starPattern = Pattern.compile("별점\\s*(\\d+)");
    Matcher matcher = starPattern.matcher(result);

    int rate = 0;
    if (matcher.find()) {
      rate = Integer.parseInt(matcher.group(1));
    } else {
      System.err.println("LLM 응답에서 별점 값을 찾지 못했습니다. 기본값 0으로 설정합니다.");
    }

    List<OcrResponseDto> ocrResponseDtoList = ocrEntityList.stream().map(
        OcrResponseDto::toDto
    ).toList();

    HealthReport healthReport = HealthReport.builder()
        .userId(userId)
        .title(year + "-" + month + "-건강 레포트 요약")
        .reportDate(LocalDate.of(year, month, 1))
        .summary(result)
        .rate(rate)
        .build();

    healthReportRepository.save(healthReport);

    System.out.println(result);
    return HealthReportResponseDto.toDto(healthReport, ocrResponseDtoList);
  }

  @Override
  @Cacheable(value = "healthReport", key = "#userId", cacheManager = "healthReportCacheManager")
  public HealthReportListResponseDto getHealthReport(Long userId) {
    List<HealthReport> healthReportList = healthReportRepository.findByUserId(userId);
    List<OcrResponseDto> ocrResultList = ocrService.getOcrResult(userId);
    List<HealthReportResponseDto> healthReportResponseDtoList = healthReportList.stream().map((report) -> {
              List<OcrResponseDto> ocrResults = ocrResultList.stream().filter((ocrResult) -> {
                        if (ocrResult.getReportDate().getYear() == report.getReportDate().getYear()) {
                          if (ocrResult.getReportDate().getMonth() == report.getReportDate().getMonth()) {
                            return true;
                          }
                        }
                        return false;
                      }
                  )
                  .toList();

              return HealthReportResponseDto.toDto(report, ocrResults);
            }
        )
        .toList();

    return new HealthReportListResponseDto(healthReportResponseDtoList);
  }

  @Override
  @Cacheable(value = "healthReportDetail", key = "#id", cacheManager = "healthReportCacheManager")
  public HealthReportResponseDto getHealthReportDetail(String id) {
    HealthReport healthReport = healthReportRepository.findByIdOrElseThrow(id);
    List<OcrEntity> ocrEntityList = ocrService.findOcrEntity(
        Long.valueOf(id),
        healthReport.getReportDate().getYear(),
        healthReport.getReportDate().getMonthValue()
    );
    List<OcrResponseDto> ocrResults = ocrEntityList.stream().map(OcrResponseDto::toDto).toList();
    return HealthReportResponseDto.toDto(healthReport, ocrResults);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "healthReport", allEntries = true, cacheManager = "healthReportCacheManager"),
      @CacheEvict(value = "healthReportDetail", key = "#id", cacheManager = "healthReportCacheManager")
  })
  public HealthReportResponseDto updateHealthReport(String id,
      UpdateHealthReportRequestDto updateHealthReportRequestDto) {
    HealthReport healthReport = healthReportRepository.findByIdOrElseThrow(id);
    healthReport.updateHealthReport(updateHealthReportRequestDto);
    healthReportRepository.save(healthReport);

    List<OcrEntity> ocrEntityList = ocrService.findOcrEntity(
        Long.valueOf(id),
        healthReport.getReportDate().getYear(),
        healthReport.getReportDate().getMonthValue()
    );
    List<OcrResponseDto> ocrResults = ocrEntityList.stream().map(OcrResponseDto::toDto).toList();

    return HealthReportResponseDto.toDto(healthReport, ocrResults);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "healthReport", allEntries = true, cacheManager = "healthReportCacheManager"),
      @CacheEvict(value = "healthReportDetail", key = "#id", cacheManager = "healthReportCacheManager")
  })
  public String deleteHealthReport(String id,
      DeleteHealthReportRequestDto deleteHealthReportRequestDto) {
    healthReportRepository.deleteByIdOrElseThrow(id);
    return "삭제되었습니다.";
  }

  @Override
  public String createHealthReportThisMonth(int year, int month) {

    List<OcrEntity> ocrEntityList = ocrService.findOcrEntityThisMonth(year, month);
    if (ocrEntityList.isEmpty()) {
      return null;
    }
    Map<Long, List<OcrEntity>> groupedByUserId = ocrEntityList.stream()
        .collect(Collectors.groupingBy(OcrEntity::getUserId));

    for (Map.Entry<Long, List<OcrEntity>> entry : groupedByUserId.entrySet()) {
      Long userId = entry.getKey();
      List<OcrEntity> userOcrList = entry.getValue();

      String prompt = buildPrompt(userOcrList, year, month);
      String result = callLLM(prompt);

      Pattern starPattern = Pattern.compile("별점\\s*(\\d+)");
      Matcher matcher = starPattern.matcher(result);

      int rate = 0;
      if (matcher.find()) {
        rate = Integer.parseInt(matcher.group(1));
      } else {
        System.err.println("LLM 응답에서 별점 값을 찾지 못했습니다. 기본값 0으로 설정합니다.");
      }

      HealthReport healthReport = HealthReport.builder()
          .userId(userId)
          .title(year + "-" + month + "-건강 레포트 요약")
          .reportDate(LocalDate.of(year, month, 1))
          .summary(result)
          .rate(rate)
          .build();

      healthReportRepository.save(healthReport);
    }

    // 스케줄러 완료 후 healthReport 캐시 전체 무효화
    evictHealthReportCache();

    return "스케줄링 완성";
  }

  private void evictHealthReportCache() {
    try {
      Set<String> keys = redisTemplate.keys("healthReport::*");
      if (keys != null && !keys.isEmpty()) {
        redisTemplate.delete(keys);
        log.info("healthReport 캐시 {}건 삭제 완료", keys.size());
      }
    } catch (Exception e) {
      log.warn("healthReport 캐시 삭제 중 오류 발생", e);
    }
  }

  private String buildPrompt(List<OcrEntity> ocrEntities, int year, int month) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        String.format("당신은 건강 리포트 작성 전문가입니다.\n%d년 %d월 한 달간의 진단 내역은 다음과 같습니다:\n\n", year, month));

    int index = 1;
    for (OcrEntity entity : ocrEntities) {
      sb.append(String.format("%d. [%s] 제목: %s\n", index++, entity.getReportDate(),
          entity.getReportTitle()));
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
    sb.append("- 리포트를 바탕으로 별점을 알려줘. (5점 만점)");
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

    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody()
        .get("choices");
    return (String) ((Map) choices.get(0).get("message")).get("content");
  }
}
