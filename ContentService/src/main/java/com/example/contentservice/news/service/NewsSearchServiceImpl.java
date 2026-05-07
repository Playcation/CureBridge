package com.example.contentservice.news.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.news.document.NewsDocument;
import com.example.contentservice.news.dto.NewsResponseDto;
import com.example.contentservice.news.dto.TopKeywordResponseDto;
import com.example.contentservice.news.entity.News;
import com.example.contentservice.news.filter.KeywordFilter;
import com.example.contentservice.news.repository.NewsRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsSearchServiceImpl implements NewsSearchService {
	private final ElasticsearchClient elasticsearchClient;
	private final NewsRepository newsRepository;

	// 제목 으로 검색하는 쿼리를 포함한 메서드
	@Override
	@Cacheable(value = "news_keyword_results", key = "#keyword", condition = "@keywordRankingService.getSearchCount(#keyword) >= 10")
	public PagingDto<NewsResponseDto> searchByTitle(String keyword, Pageable pageable) {
		String fieldSuffix = containsKorean(keyword) ? "korean" : "english";
		String ngramField = "ngram";
		try {
			SearchResponse<NewsDocument> response = elasticsearchClient.search(s -> s
					.index("news-index-v2")
					.trackTotalHits(t -> t.enabled(true))
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
	public List<TopKeywordResponseDto> aggregateTopKeywordsForDateRange(LocalDate gte, LocalDate lt,
		int size) {
		try {
			SearchResponse<Void> response = elasticsearchClient.search(s -> s
					.index("news-index-v2")
					.size(0)
					.query(q -> q.range(r -> r
						.date(d -> d
							.field("publishedAt")
							.gte(gte.toString())
							.lt(lt.toString())
						)
					))
					.aggregations("top_keywords", a -> a
						.terms(t -> t
							.field("title.korean")
							.size(size)
							.exclude(e -> e.terms(new ArrayList<>(KeywordFilter.EXCLUDED_KEYWORDS))))
					),
				Void.class
			);

      var aggregate = response.aggregations().get("top_keywords");

      if (aggregate == null) {
        return List.of();
      }

			List<String> candidateKeywords = aggregate.sterms().buckets().array().stream()
				.map(b -> b.key().stringValue())
				.filter(k -> k.length() >= 2 && k.matches("[가-힣a-zA-Z0-9]+"))
				.limit(10) // 상위 10개만 확정
				.toList();

			return candidateKeywords.stream()
				.map(keyword -> new TopKeywordResponseDto(
					keyword,
					countBySearch(keyword, LocalDate.now().minusDays(7), LocalDate.now().plusDays(1)) // 검색 결과 수와 일치시킴
				))
				.sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
				.toList();


    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch 키워드 집계 중 오류 발생", e);
    }
  }

  private long countBySearch(String keyword, LocalDate gte, LocalDate lt) {
    try {
      SearchResponse<Void> response = elasticsearchClient.search(s -> s
              .index("news-index-v2")
              .size(0)
              .query(q -> q.bool(b -> b
                  .must(m -> m.match(mt -> mt
                      .field("title.korean")
                      .query(keyword)
                  ))
                  .filter(f -> f.range(r -> r
                      .date(d -> d
                          .field("publishedAt")
                          .gte(gte.toString())
                          .lt(lt.toString())
                      )
                  ))
              )),
          Void.class
      );

			return response.hits().total() == null
				? 0
				: response.hits().total().value();

		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 키워드 count 검색 중 오류 발생. keyword=" + keyword, e);
		}
	}

	@Override
	public void deleteOldNews(int days) {
		try {
			// 삭제 기준 날짜 (예: 15일 전)
			String olderThan = LocalDate.now().minusDays(days).toString();

			elasticsearchClient.deleteByQuery(d -> d
				.index("news-index-v2")
				.query(q -> q
					.range(r -> r
						.date(dt -> dt
							.field("publishedAt")
							.lt(olderThan) // 기준일 미만 데이터 삭제
						)
					)
				)
			);
		} catch (IOException e) {
			throw new RuntimeException("ES 삭제 중 오류 발생", e);
		}
	}

}
