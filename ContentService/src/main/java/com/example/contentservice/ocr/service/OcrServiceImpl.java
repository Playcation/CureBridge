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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

      Map<String, String> ocrMap = extractTitleAndDate(result);
      OcrEntity ocrEntity = OcrEntity.builder()
          .reportTitle(ocrMap.getOrDefault("title", "진단서"))
          .reportDate(ocrMap.getOrDefault("date", LocalDateTime.now().toString()))
          .parsedText(result)
          .updatedText(result)
          .userId(userId)
          .build();
      ocrRepository.save(ocrEntity);
      return OcrResponseDto.toDto(ocrEntity);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public OcrResponseDto updateOcrResult(Long id, UpdateRequestDto updateRequestDto) {
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
  public OcrResponseDto getOcrDetalResult(Long id) {
    OcrEntity ocrEntity = ocrRepository.findByIdOrElseThrow(id);
    return OcrResponseDto.toDto(ocrEntity);
  }

  @Override
  public String deleteOcrResult(Long id, DeleteRequestDto deleteRequestDto) {
    ocrRepository.deleteById(id);
    return "삭제되었습니다.";
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

  private Map<String, String> extractTitleAndDate(String ocrResultJson) {
    JSONObject result = new JSONObject(ocrResultJson);
    JSONArray fields = result.getJSONArray("images")
        .getJSONObject(0)
        .getJSONArray("fields");

    String title = null;
    String date = null;

    // 날짜 정규표현식
    Pattern datePattern = Pattern.compile(
        "(\\d{4}[.-년]\\s?\\d{1,2}[.-월]\\s?\\d{1,2}[일]?)"  // 2025년 07월 22일 or 2025-07-22 or 2025.07.22
    );

    for (int i = 0; i < fields.length(); i++) {
      JSONObject field = fields.getJSONObject(i);
      String text = field.getString("inferText");
      double confidence = field.getDouble("inferConfidence");

      if (confidence < 0.7) continue;

      // 제목 추정
      if (title == null && (text.contains("진단서") || text.contains("검진") || text.contains("소견서") || text.contains("병명"))) {
        title = text;
      }

      // 날짜 추출
      Matcher matcher = datePattern.matcher(text);
      if (date == null && matcher.find()) {
        date = matcher.group(1).replaceAll("\\s+", "");
      }

      // 모두 찾으면 종료
      if (title != null && date != null) break;
    }
    if (title == null){
      title = "진단서";
    }
    if(date == null){
      date = LocalDateTime.now().toString();
    }
    Map<String, String> resultMap = new HashMap<>();
    resultMap.put("title", title);
    resultMap.put("date", date);
    return resultMap;
  }
}
