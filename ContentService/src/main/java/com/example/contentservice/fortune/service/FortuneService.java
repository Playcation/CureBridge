package com.example.contentservice.fortune.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FortuneService {

    private final ObjectMapper om;
    // RestTemplate은 Bean으로 등록하여 사용하는 것이 좋습니다.
    // @Configuration 클래스에 @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.ai-model}")
    private String aiModel;

    public String getFortune(MultipartFile image, String birthDateYYYYMMDD) {
        try {
            // 1. 생년월일 파싱 (실패해도 계속 진행)
            parseBirthDate(birthDateYYYYMMDD);

            // 2. 이미지 -> Base64 데이터 URL로 변환
            String dataUrl = encodeImageToDataUrl(image);

            // 3. 시스템 및 사용자 프롬프트 생성
            String systemPrompt = buildSystemPrompt();
            String userPrompt = buildUserPrompt(birthDateYYYYMMDD);

            // 4. OpenAI Vision API 호출
            String jsonResponse = callVisionLLM(systemPrompt, userPrompt, dataUrl);

            // 5. 결과 파싱 및 반환
            return parseAndFormatResponse(jsonResponse);

        } catch (IOException e) {
            log.error("Failed to process image file.", e);
            return "이미지 처리 중 오류가 발생했습니다.";
        } catch (Exception e) {
            log.error("An unexpected error occurred while getting fortune.", e);
            return "운세 생성 중 오류가 발생했어요: " + e.getMessage();
        }
    }

    private void parseBirthDate(String birthDateYYYYMMDD) {
        if (birthDateYYYYMMDD != null && !birthDateYYYYMMDD.isEmpty()) {
            try {
                LocalDate.parse(birthDateYYYYMMDD, DateTimeFormatter.ofPattern("yyyyMMdd"));
            } catch (Exception e) {
                log.warn("Could not parse birth date: {}. Proceeding without it.", birthDateYYYYMMDD);
            }
        }
    }

    private String encodeImageToDataUrl(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 필요합니다.");
        }
        String contentType = image.getContentType() != null ? image.getContentType() : "image/jpeg";
        String base64 = Base64.getEncoder().encodeToString(image.getBytes());
        return "data:" + contentType + ";base64," + base64;
    }

    private String buildSystemPrompt() {
        return "너는 세련된 한국어 점성가다. 사용자가 제공한 사진(표정/분위기)과 생년월일 정보를 참고해 "
            + "오늘의 운세를 간단하고 즐겁게 알려줘. 얼굴 인식/신원 식별/민감 특성 추론(인종, 종교, 건강, 성적지향 등)은 절대 하지 마. "
            + "점술적 조언은 가볍고 긍정적으로, 의학·법률·금융에 대한 확정적 조언은 피하고 안전을 최우선으로 해. "
            + "출력은 반드시 요청된 JSON 형식이어야 한다.";
    }

    private String buildUserPrompt(String birthDateYYYYMMDD) {
        LocalDate todaySeoul = LocalDate.now(ZoneId.of("Asia/Seoul"));
        String todayFormatted = todaySeoul.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));

        return "" +
"아래 정보를 기반으로 '오늘의 운세'를 한국어로 만들어줘.\n" + 
"\n" + 
"                - 오늘 날짜(Asia/Seoul): %s\n" + 
"                - 생년월일(YYYYMMDD): %s\n" + 
"                - 사진: (첨부 이미지 참조)\n" + 
"\n" + 
"                출력은 반드시 아래 JSON 형식의 한 객체로만:\n" + 
"                {\n" + 
"                  \"sign\": \"양자리/황소자리 등 서양 별자리(가능하면 추정, 모르면 빈 문자열)\",\n" + 
"                  \"summary\": \"한 문단 요약 (친근한 어투, 3~5문장)\",\n" + 
"                  \"lucky_color\": \"예: 네이비 블루\",\n" + 
"                  \"lucky_number\": 7,\n" + 
"                  \"do\": [\"해도 좋은 일\",\"작은 습관 한 가지\"],\n" + 
"                  \"dont\": [\"피하면 좋은 일\",\"주의할 대화/상황 한 가지\"]\n" + 
"                }\n" + 
"\n" + 
"                주의:\n" + 
"                - 사진으로 신원/민감 특성 추론 금지.\n" + 
"                - 과장된 단정 대신 실천 가능한 조언 위주.\n" + 
"                - 전부 한국어로.\n" + 
"                ".formatted(todayFormatted, birthDateYYYYMMDD != null ? birthDateYYYYMMDD : "정보 없음");
    }

    private String callVisionLLM(String systemPrompt, String userPrompt, String dataUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> userMessageContent = Map.of(
            "type", "text",
            "text", userPrompt
        );
        Map<String, Object> imageContent = Map.of(
            "type", "image_url",
            "image_url", Map.of("url", dataUrl)
        );

        Map<String, Object> body = Map.of(
            "model", aiModel,
            "response_format", Map.of("type", "json_object"),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", List.of(userMessageContent, imageContent))
            )
        );

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            "https://api.openai.com/v1/chat/completions", request, String.class
        );

        return response.getBody();
    }

    private String parseAndFormatResponse(String jsonResponse) {
        try {
            JsonNode rootNode = om.readTree(jsonResponse);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).path("message");
                String content = message.path("content").asText();
                // content는 이미 JSON 형식의 문자열이므로, 그대로 반환하거나
                // 혹은 파싱하여 애플리케이션 내부 모델로 변환할 수 있습니다.
                // 여기서는 JSON 문자열 그대로 반환합니다.
                return content;
            }
            return "운세 정보를 받아오지 못했습니다.";
        } catch (Exception e) {
            log.error("Failed to parse OpenAI response: {}", jsonResponse, e);
            // 모델이 JSON을 벗어나면 그대로 반환
            return jsonResponse.isEmpty() ? "오늘의 운세 생성에 실패했어요. 잠시 후 다시 시도해주세요." : jsonResponse;
        }
    }
}