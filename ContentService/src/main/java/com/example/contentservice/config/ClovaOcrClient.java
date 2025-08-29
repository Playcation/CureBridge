package com.example.contentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class ClovaOcrClient {

    @Value("${ocr.secretKey}")
    private String SECRET_KEY;
    @Value("${ocr.apiUrl}")
    private String API_URL;

    public String requestOcr(File file) throws IOException {
        String boundary = "----" + UUID.randomUUID().toString().replace("-", "");

        // 1. JSON 메시지 구성
        org.json.JSONObject message = new org.json.JSONObject();
        message.put("version", "V2");
        message.put("requestId", UUID.randomUUID().toString());
        message.put("timestamp", System.currentTimeMillis());
        message.put("lang", "ko");

        org.json.JSONObject image = new org.json.JSONObject();
        image.put("format", "jpg");
        image.put("name", file.getName());

        org.json.JSONArray images = new org.json.JSONArray();
        images.put(image);
        message.put("images", images);

        // 2. API 호출
        return sendOcrRequest(file, message.toString(), boundary);
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
