package com.example.contentservice.news.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.service.NewsCacheService;
import com.example.contentservice.news.service.NewsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsCreateScheduler {

	private final NewsService newsService;
	private final NewsCacheService newsCacheService;
	private final ObjectMapper objectMapper;

  @Value("${newsapi.clientId}")
  private String CLIENT_ID;

  @Value("${newsapi.clientSecret}")
  private String CLIENT_SECRET;

	// 매일 자정(0시 0분)에 이 메서드가 자동으로 실행됩니다.
	@Scheduled(cron = "00 00 00 * * *", zone = "Asia/Seoul")
	public void newsapi() {
		LocalDate today = LocalDate.now();
		List<NewsRequestDto> dtoList = new ArrayList<>();
		for (int start = 1; start <= 1000; start += 100) {
			try {
				String query = "의료 의학";
				String apiURL =
					"https://openapi.naver.com/v1/search/news?query=" + URLEncoder.encode(query, "UTF-8")
						+ "&display=100&start=" + start;

        HttpURLConnection conn = (HttpURLConnection) new URL(apiURL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
        conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

        BufferedReader br = new BufferedReader(new InputStreamReader(
            conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream()
        ));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
        br.close();

        JsonNode items = objectMapper.readTree(sb.toString()).get("items");

        for (JsonNode item : items) {
          String title = item.get("title").asText().replaceAll("<.*?>", ""); // 태그 제거
          String link = item.get("link").asText();
          String pubDate = item.get("pubDate").asText(); // "Wed, 24 Jul 2025 08:00:00 +0900"

          dtoList.add(new NewsRequestDto(title, link, pubDate));
        }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
		dtoList.sort((a, b) -> {
			ZonedDateTime dtA = ZonedDateTime.parse(a.getPubDate(), formatter);
			ZonedDateTime dtB = ZonedDateTime.parse(b.getPubDate(), formatter);
			return dtA.compareTo(dtB); // 과거 -> 최신 순 정렬
		});
		if (!dtoList.isEmpty()) {
			// 뉴스 업데이트 및 캐시 갱신
			newsService.saveRecentNews(dtoList);
			newsCacheService.refreshNewsCache(today);
		}
	}
}
