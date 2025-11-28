package com.example.contentservice.news.controller;

import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.utils.JwtParser;
import com.example.contentservice.news.dto.NewsRequestDto;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.repository.NewsRepository;
import com.example.contentservice.news.service.NewsSearchService;
import com.example.contentservice.news.service.NewsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

  /* (추가) URL에서 도메인 추출해서 언론사 긁어오기 (도전해보기) */
  private final NewsService newsService;
  private final NewsSearchService newsSearchService;
  private final ObjectMapper objectMapper;
  private final NewsRepository newsRepository;
  private final JwtParser jwtParser;


  @Value("${newsapi.clientId}")
  private String CLIENT_ID;

  @Value("${newsapi.clientSecret}")
  private String CLIENT_SECRET;

  // 매일 자정(0시 0분)에 이 메서드가 자동으로 실행됩니다.
  @Scheduled(cron = "0 54 20 * * *")
  public void newsapi() {
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
      }
    }
  }

  // 게시물 다건 조회
  @GetMapping
  public ResponseEntity<PagingDto<NewsResponseDto>> getNewsAndPaging(
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    PagingDto<NewsResponseDto> newsList = newsService.getNewsAndPaging(pageable);
    return new ResponseEntity<>(newsList, HttpStatus.OK);
  }

  // 게시물 삭제
  @DeleteMapping("/{newsId}")
  public ResponseEntity<String> deleteById(@PathVariable Long newsId,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader) {     /* (추가) 토큰으로 관리자 인증 */
    jwtParser.checkAdmin(authorizationHeader);
    newsService.deleteNews(newsId);
    return new ResponseEntity<>("게시물이 삭제되었습니다.", HttpStatus.OK);
  }

  @GetMapping("/keywords")
  public List<String> getAllNewsTitles() {
    return newsRepository.findAll().stream()
        .map(News::getTitle)
        .collect(Collectors.toList());
  }

  // 제목 으로 검색
  @GetMapping("/search-title")
  public ResponseEntity<PagingDto<NewsResponseDto>> searchByTitleAndPaging(
      @RequestParam("keyword") String keyword,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
  ) {

    PagingDto<NewsResponseDto> result = newsSearchService.searchByTitle(keyword,
        pageable);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }


  // 특정 기간 내 인기 키워드 조회 API
  @GetMapping("/top-keywords")
  public ResponseEntity<List<StringTermsBucket>> getTopKeywordsForDateRange(
      @RequestParam(value = "gte", required = false) String gte,
      @RequestParam(value = "lt", required = false) String lt,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    LocalDate startDate = (gte != null) ? LocalDate.parse(gte) : LocalDate.now().minusDays(7);
    LocalDate endDate = (lt != null) ? LocalDate.parse(lt) : LocalDate.now();

    try {
      List<StringTermsBucket> topKeywords = newsSearchService.aggregateTopKeywordsForDateRange(
          startDate, endDate, size);
      return new ResponseEntity<>(topKeywords, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
