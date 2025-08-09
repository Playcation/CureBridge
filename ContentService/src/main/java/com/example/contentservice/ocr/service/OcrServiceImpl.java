package com.example.contentservice.ocr.service;

import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrMultiResponseDto;
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

  @Value("${ocr.secretKey}")
  private String SECRET_KEY;
  @Value("${ocr.apiUrl}")
  private String API_URL;

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

      String boundary = "----" + UUID.randomUUID().toString().replace("-", "");

      // 1. JSON 메시지 구성
      JSONObject message = new JSONObject();
      message.put("version", "V2");
      message.put("requestId", UUID.randomUUID().toString());
      message.put("timestamp", System.currentTimeMillis());
      message.put("lang", "ko");

      JSONObject image = new JSONObject();
      image.put("format", "jpg");
      image.put("name", file.getName());

      JSONArray images = new JSONArray();
      images.put(image);
      message.put("images", images);

      // 2. API 호출
      String result = sendOcrRequest(file, message.toString(), boundary);

      List<String> lines = extractInferLines(result);
      for (String line : lines) {
        System.out.println("줄: " + line);
      }

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
  public List<OcrMultiResponseDto> getOcrResult(Long userId) {
    List<OcrEntity> ocrEntityList = ocrRepository.findByUserId(userId);
    return ocrEntityList.stream().map(OcrMultiResponseDto::toDto).toList();
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

  private String sendOcrRequest(File file, String jsonMessage, String boundary) throws IOException {
    URL url = new URL(API_URL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setReadTimeout(30000);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    connection.setRequestProperty("X-OCR-SECRET", SECRET_KEY);

    try (DataOutputStream dos = new DataOutputStream(connection.getOutputStream())) {
      writeMultiPart(dos, jsonMessage, file, boundary);
    }

    int responseCode = connection.getResponseCode();
    InputStream responseStream = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(responseStream));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null) {
      response.append(line);
    }
    br.close();

    return response.toString();
  }

  private void writeMultiPart(OutputStream out, String jsonMessage, File file, String boundary) throws IOException {
    String lineEnd = "\r\n";
    String twoHyphens = "--";

    StringBuilder sb = new StringBuilder();
    sb.append(twoHyphens).append(boundary).append(lineEnd);
    sb.append("Content-Disposition: form-data; name=\"message\"").append(lineEnd).append(lineEnd);
    sb.append(jsonMessage).append(lineEnd);
    out.write(sb.toString().getBytes(StandardCharsets.UTF_8));

    if (file != null && file.exists()) {
      out.write((twoHyphens + boundary + lineEnd).getBytes(StandardCharsets.UTF_8));
      String fileHeader = "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + lineEnd +
          "Content-Type: application/octet-stream" + lineEnd + lineEnd;
      out.write(fileHeader.getBytes(StandardCharsets.UTF_8));

      try (FileInputStream fis = new FileInputStream(file)) {
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
        }
        out.write(lineEnd.getBytes(StandardCharsets.UTF_8));
      }
    }

    out.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes(StandardCharsets.UTF_8));
    out.flush();
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
    String diagnosisDate = null;
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

      // 날짜 추출 (진단일 or 발행일 등)
      if (diagnosisDate == null &&
          (line.contains("진단일") || line.contains("발행일") || line.contains("확인함") || line.contains("가료") || line.contains("통원") || line.contains("입원") || line.contains("내원일"))) {
        Matcher matcher = datePattern.matcher(line);
        if (matcher.find()) {
          String raw = matcher.group().replaceAll("[^0-9]+", "-"); // 연속된 구분자 하나로
          String[] parts = raw.split("-");
          if (parts.length >= 3) {
            try {
              System.out.println(Arrays.toString(parts));
              int year = Integer.parseInt(parts[0]);
              int month = Integer.parseInt(parts[1]);
              int day = Integer.parseInt(parts[2]);
              diagnosisDate = String.format("%04d-%02d-%02d", year, month, day);
            } catch (NumberFormatException e) {
              // 로그 남기기 또는 무시
              System.err.println("날짜 파싱 오류: " + raw);
            }
          }
        }
      }

      // 성명 추출
      if (patientName == null && (line.contains("성명") || line.contains("이름"))) {
        Matcher nameMatcher = Pattern.compile("성명[:\\s]*([가-힣]{2,5})").matcher(line);
        if (nameMatcher.find()) {
          patientName = nameMatcher.group(1);
        } else {
          // '성명 홍길동 성별' 형식
          String[] parts = line.split("\\s+");
          for (int p = 0; p < parts.length - 1; p++) {
            if ((parts[p].contains("성명") || parts[p].contains("이름"))
                && parts[p + 1].matches("[가-힣]{2,5}")) {
              patientName = parts[p + 1];
              break;
            }
          }
        }
      }

      // 병명 추출
      if (line.contains("병명") || line.contains("진단명") || line.contains("신생물")) {
        if (i + 1 < lines.size()) {
          String dx = lines.get(i + 1).replaceAll("\\s+", "");
          if (dx.length() > 1 && dx.matches(".*[가-힣]+.*")) {
            diagnoses.add(dx);
          }
        }
        // ICD 코드 포함 줄도 병명에 추가
        if (line.matches(".*[A-Z][0-9]{2,3}.*")) {
          diagnoses.add(line.trim());
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
