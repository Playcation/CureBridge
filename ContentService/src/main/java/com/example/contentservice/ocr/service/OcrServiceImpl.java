package com.example.contentservice.ocr.service;

import com.example.contentservice.ocr.dto.UploadRequestDto;
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
import java.util.UUID;
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
  public String analyzeImageWithClovaOcr(MultipartFile multipartFile) {
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
      return sendOcrRequest(file, message.toString(), boundary);
    } catch (Exception e) {
      return "{\"error\":\"" + e.getMessage() + "\"}";
    }
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
}
