package com.example.contentservice.news.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.news.document.NewsDocument;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.repository.NewsRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSearchServiceImpl implements NewsSearchService {

  private final ElasticsearchClient elasticsearchClient;
  private final NewsRepository newsRepository;
  private final JdbcTemplate jdbcTemplate;
  private static final String[] SECTIONS = {
      "politics", "economy", "society", "culture", "tech", "entertainment", "opinion"
  };

  // 제목 으로 검색하는 쿼리를 포함한 메서드
  @Override
  public PagingDto<NewsResponseDto> searchByTitle(String keyword, Pageable pageable) {
    String fieldSuffix = containsKorean(keyword) ? "korean" : "english";
    String ngramField = "ngram";
    try {
      SearchResponse<NewsDocument> response = elasticsearchClient.search(s -> s
              .index("news-index")
              .from(pageable.getPageNumber() * pageable.getPageSize())
              .size(pageable.getPageSize())
              .query(q -> q
                  .bool(b -> b
                      .should(sb -> sb.match(m -> m
                          .field("title." + fieldSuffix)
                          .query(keyword)
                      ))
                      .should(sb -> sb.match(m -> m
                          .field("title.ngram")
                          .query(keyword)
                      ))
                      .should(sb -> sb.match(m -> m.field("title." + ngramField).query(keyword)))
                  )
              )
              .sort(sort -> sort
                  .field(f -> f
                      .field("publishedAt")
                      .order(SortOrder.Desc)
                  )
              )
              .size(pageable.getPageSize()),
          NewsDocument.class
      );

      List<NewsResponseDto> list = response.hits().hits().stream()
          .map(hit -> {
            Long newsId = Long.valueOf(hit.id()); // hit의 ID를 News ID로 사용
            News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));
            return NewsResponseDto.fromDocument(hit.source(), news);
          })
          .collect(Collectors.toList());

      // PagingDto에 검색 결과 리스트와 전체 개수를 담아 반환
      long totalCount = response.hits().total().value();
      return new PagingDto<>(list, totalCount);

    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
    }
  }

  public boolean containsKorean(String input) {
    return input != null && input.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
  }

  // 날짜 범위 내 인기 키워드(terms aggregation) 추출
  @Override
  public List<StringTermsBucket> aggregateTopKeywordsForDateRange(LocalDate gte, LocalDate lt,
      int size) {
    try {
      SearchResponse<Void> response = elasticsearchClient.search(s -> s
          .index("news-index-nori")
          .size(0)
          .sort(sort -> sort
              .field(f -> f
                  .field("publishedAt")
                  .order(SortOrder.Desc)
              )
          )
          .aggregations("top_combined_keywords", a -> a
              .terms(t -> t
                  .field("combinedTokens")
                  .size(size)
              )
          ), Void.class);

      return response.aggregations()
          .get("top_combined_keywords")
          .sterms()
          .buckets()
          .array();
    } catch (IOException e) {
      log.error("Elasticsearch 키워드 집계 중 오류 발생", e);
      throw new RuntimeException("Elasticsearch 키워드 집계 중 오류 발생", e);
    }
  }
}
