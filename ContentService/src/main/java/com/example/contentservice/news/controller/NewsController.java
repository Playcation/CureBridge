package com.example.contentservice.news.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.service.NewsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

	private final NewsService newsService;
	private final ObjectMapper objectMapper;

	private static final String CLIENT_ID = "hwIz5ZPnJ1CnkuCntetz";
	private static final String CLIENT_SECRET = "Y3wPA82pKT";

	@GetMapping("/crawl")
	public ResponseEntity<String> newsapi() {
		for (int start = 1; start <= 1000; start += 100) {
			try {
				String query = "의료 의학";
				String apiURL = "https://openapi.naver.com/v1/search/news?query=" + URLEncoder.encode(query, "UTF-8")
					+ "&display=100&start=" + start;

				HttpURLConnection conn = (HttpURLConnection)new URL(apiURL).openConnection();
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

				List<NewsRequestDto> dtoList = new ArrayList<>();
				for (JsonNode item : items) {
					String title = item.get("title").asText().replaceAll("<.*?>", ""); // 태그 제거
					String link = item.get("link").asText();
					String pubDate = item.get("pubDate").asText(); // "Wed, 24 Jul 2025 08:00:00 +0900"

					dtoList.add(new NewsRequestDto(title, link, pubDate));
				}

				newsService.saveRecentNews(dtoList);

			} catch (Exception e) {
				e.printStackTrace();
				return ResponseEntity.internalServerError().body("뉴스 저장 중 오류 발생");
			}
		}

		return ResponseEntity.ok("24시간 이내 뉴스 저장 완료");
	}

	private static String get(String apiUrl, Map<String, String> requestHeaders) {
		HttpURLConnection con = connect(apiUrl);
		try {
			con.setRequestMethod("GET");
			for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
				con.setRequestProperty(header.getKey(), header.getValue());
			}

			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
				return readBody(con.getInputStream());
			} else { // 오류 발생
				return readBody(con.getErrorStream());
			}
		} catch (IOException e) {
			throw new RuntimeException("API 요청과 응답 실패", e);
		} finally {
			con.disconnect();
		}
	}

	private static HttpURLConnection connect(String apiUrl) {
		try {
			URL url = new URL(apiUrl);
			return (HttpURLConnection)url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
		} catch (IOException e) {
			throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
		}
	}

	private static String readBody(InputStream body) {
		InputStreamReader streamReader = new InputStreamReader(body);

		try (BufferedReader lineReader = new BufferedReader(streamReader)) {
			StringBuilder responseBody = new StringBuilder();

			String line;
			while ((line = lineReader.readLine()) != null) {
				responseBody.append(line);
			}

			return responseBody.toString();
		} catch (IOException e) {
			throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
		}
	}

	// 게시물 다건 조회
	@GetMapping
	public ResponseEntity<PagingDto<NewsResponseDto>> getNewsAndPaging(
		@RequestParam(defaultValue = "0") int page) {
		PagingDto<NewsResponseDto> newsList = newsService.getNewsAndPaging(page);
		return new ResponseEntity<>(newsList, HttpStatus.OK);
	}

	@DeleteMapping("/{newsId}")
	public ResponseEntity<String> deleteById(@PathVariable Long newsId) {
		newsService.deleteNews(newsId);
		return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
	}
}
