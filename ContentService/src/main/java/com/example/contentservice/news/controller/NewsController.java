package com.example.contentservice.news.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.utils.JwtParser;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;
import com.example.contentservice.news.service.KeywordRankingService;
import com.example.contentservice.news.service.NewsCacheService;
import com.example.contentservice.news.service.NewsSearchService;
import com.example.contentservice.news.service.NewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

	private final NewsService newsService;
	private final NewsSearchService newsSearchService;
	private final NewsCacheService newsCacheService;
	private final KeywordRankingService keywordRankingService;
	private final JwtParser jwtParser;

  // 게시물 다건 조회
  @GetMapping
  public ResponseEntity<PagingDto<NewsResponseDto>> getNewsAndPaging(
      @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
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

	// 제목 으로 검색
	@GetMapping("/search-title")
	public ResponseEntity<PagingDto<NewsResponseDto>> searchByTitleAndPaging(
		@RequestParam("keyword") String keyword,
		@PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		keywordRankingService.incrementSearchCount(keyword); // 검색량 +1
		PagingDto<NewsResponseDto> result = newsSearchService.searchByTitle(keyword,
			pageable);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// 특정 기간 내 인기 키워드 조회 API
	@GetMapping("/top-keywords")
	public ResponseEntity<List<TopKeywordResponseDto>> getTopKeywordsForDateRange(
		@RequestParam(value = "gte", required = false) String gte,
		@RequestParam(value = "lt", required = false) String lt,
		@RequestParam(value = "size", defaultValue = "50") int size
	) {
		LocalDate startDate = (gte != null) ? LocalDate.parse(gte) : LocalDate.now().minusDays(7);
		LocalDate endDate = (lt != null) ? LocalDate.parse(lt) : LocalDate.now().plusDays(1);

		try {
			List<TopKeywordResponseDto> topKeywords =
				newsCacheService.getCachedTopKeywords(startDate, endDate, size);

      return ResponseEntity.ok(topKeywords);
    } catch (Exception e) {
      log.error("top-keywords 조회 실패. startDate={}, endDate={}, size={}", startDate, endDate, size,
          e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
