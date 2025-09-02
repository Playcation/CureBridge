package com.example.contentservice.ocr.service;

import com.example.contentservice.config.ClovaOcrClient;
import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import com.example.contentservice.ocr.dto.UploadRequestDto;
import com.example.contentservice.ocr.entity.OcrEntity;
import com.example.contentservice.ocr.repository.OcrRepository;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

  private final OcrRepository ocrRepository;
  private final ClovaOcrClient clovaOcrClient;

  private final List<Pattern> SENSITIVE_PATTERNS = List.of(
      Pattern.compile(".*(이름|성명|환자명).*"),
      Pattern.compile(".*(생년월일|출생).*"),
      Pattern.compile(".*(주민등록번호|주민번호).*"),
      Pattern.compile(".*(전화|연락처).*"),
      Pattern.compile(".*(주소).*"),
      Pattern.compile("\\d{2,3}-\\d{3,4}-\\d{4}"),  // 전화번호
      Pattern.compile("\\d{6}-\\d{7}")              // 주민번호
  );

  private final List<String> TITLE_KEYWORDS = Arrays.asList(
      "진단서", "수술확인서", "진단확인서", "처방전", "소견서", "진료", "확인서"
  );

  @Override
  public OcrResponseDto analyzeImageWithClovaOcr(Long userId, MultipartFile multipartFile) {
    try {
      File file = File.createTempFile("upload-", ".jpg");
      multipartFile.transferTo(file);

      String result = clovaOcrClient.requestOcr(file);

      List<String> lines = extractInferLines(result);

      Map<String, Object> ocrMap = extractReportInfo(lines);
      List<String> rawText = removeSensitiveLines(lines);
      OcrEntity ocrEntity = OcrEntity.builder()
          .reportTitle(ocrMap.getOrDefault("reportTitle", "진단서 기본").toString())
          .reportDate(LocalDate.parse(ocrMap.getOrDefault("reportDate", LocalDate.now()).toString()))
          .patientName(ocrMap.getOrDefault("patientName", "이름 기본").toString())
          .diagnosis(ocrMap.getOrDefault("diagnosis", "병명 기본").toString())
          .rawText(rawText)
          .userId(userId)
          .build();
      OcrEntity savedEntity = ocrRepository.insert(ocrEntity);
      return OcrResponseDto.toDto(savedEntity);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public OcrResponseDto updateOcrResult(String id, UpdateRequestDto updateRequestDto) {
    OcrEntity ocrEntity = ocrRepository.findByIdOrElseThrow(id);
    ocrEntity.updateOcr(updateRequestDto);
    ocrRepository.save(ocrEntity);
    return OcrResponseDto.toDto(ocrEntity);
  }

  @Override
  public List<OcrResponseDto> getOcrResult(Long userId) {
    List<OcrEntity> ocrEntityList = ocrRepository.findByUserId(userId);
    return ocrEntityList.stream().map(OcrResponseDto::toDto).toList();
  }

  @Override
  public OcrResponseDto getOcrDetalResult(String id) {
    OcrEntity ocrEntity = ocrRepository.findByIdOrElseThrow(id);
    return OcrResponseDto.toDto(ocrEntity);
  }

  @Override
  public String deleteOcrResult(String id, DeleteRequestDto deleteRequestDto) {
    ocrRepository.deleteById(id);
    return "삭제되었습니다.";
  }

  @Override
  public List<OcrEntity> findOcrEntity(Long userId, int year, int month) {
    LocalDate start = LocalDate.of(year, month, 1);
    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

    return ocrRepository.findByUserIdAndReportDateBetween(userId, start, end);
  }

  

  private boolean containsSensitiveInfo(String line) {
    return SENSITIVE_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(line).find());
  }

  private List<String> removeSensitiveLines(List<String> lines) {
    return lines.stream()
        .filter(line -> !containsSensitiveInfo(line))
        .toList();
  }

  private List<String> extractInferLines(String ocrResultJson) {
    List<String> lines = new ArrayList<>();
    StringBuilder currentLine = new StringBuilder();

    JSONObject result = new JSONObject(ocrResultJson);
    JSONArray fields = result.getJSONArray("images")
        .getJSONObject(0)
        .getJSONArray("fields");

    for (int i = 0; i < fields.length(); i++) {
      JSONObject field = fields.getJSONObject(i);
      String inferText = field.optString("inferText", "");
      double inferConfidence = field.optDouble("inferConfidence", 0.0);
      boolean lineBreak = field.optBoolean("lineBreak", false);

      if (inferConfidence >= 0.7 && !inferText.isEmpty()) {
        currentLine.append(inferText).append(" ");
      }

      if (lineBreak) {
        lines.add(currentLine.toString().trim()); // 줄 단위로 추가
        currentLine.setLength(0); // 초기화
      }
    }

    // 마지막 줄 추가 (lineBreak 없이 끝났을 경우)
    if (!currentLine.isEmpty()) {
      String line = currentLine.toString().trim();
    }

    return lines;
  }

  private Map<String, Object> extractReportInfo(List<String> lines) {
    String title = null;
    LocalDate diagnosisDate = null;
    String patientName = null;
    List<String> diagnoses = new ArrayList<>();

    Pattern datePattern = Pattern.compile(
        "(\\d{4}[년.-]\\s*\\d{1,2}[월.-]\\s*\\d{1,2}[일]?)|(\\d{4}[-.]\\d{1,2}[-.]\\d{1,2})"
    );

    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i).replaceAll("\\s+", " ").trim();

      // 제목 추출 (가장 앞쪽 줄, KEYWORD 포함 + '확인서' 등)
      if (title == null) {
        for (String keyword : TITLE_KEYWORDS) {
          if (line.contains(keyword)) {
            title = line;
            break;
          }
        }
      }

      // 날짜 추출
      if (diagnosisDate == null) { // 아직 날짜를 찾지 못했다면
        Matcher dateMatcher = datePattern.matcher(line);
        if (dateMatcher.find()) {
          String raw = dateMatcher.group().replaceAll("[^0-9]+", "-");
          String[] parts = raw.split("-");
          if (parts.length >= 3) {
            try {
              int year = Integer.parseInt(parts[0]);
              int month = Integer.parseInt(parts[1]);
              int day = Integer.parseInt(parts[2]);
              diagnosisDate = LocalDate.of(year, month, day);
              break;
            } catch (NumberFormatException e) {
              System.err.println("날짜 파싱 오류: " + raw);
            }
          }
        }
      }

      // 성명 추출
      if (patientName == null) {
        String[] parts = line.split("\\s+");
        for (int p = 0; p < parts.length - 1; p++) {
          if ((parts[p].contains("성명") || parts[p].endsWith("명") || parts[p].endsWith("름"))
              && parts[p + 1].matches("[가-힣]{2,5}")) {
            patientName = parts[p + 1];
            break;
          }
        }
      }

      // 병명 추출
      if (line.contains("병명") || line.contains("진단명") || line.contains("신생물")) {
        // 같은 줄에서 진단 추출
        Pattern diagnosisPattern = Pattern.compile("(병명|진단명|신생물)\s*([가-힣]+)");
        Matcher diagnosisMatcher = diagnosisPattern.matcher(line);
        if (diagnosisMatcher.find()) {
          diagnoses.add(diagnosisMatcher.group(2));
        }
      }
    }

    Map<String, Object> result = new HashMap<>();
    result.put("reportTitle", title != null ? title : "제목 미확인");
    result.put("reportDate", diagnosisDate != null ? diagnosisDate : LocalDate.now());
    result.put("patientName", patientName != null ? patientName : "이름 미확인");
    result.put("diagnosis", diagnoses.isEmpty() ? "병명 미확인" : String.join(", ", diagnoses));
    return result;
  }
}
