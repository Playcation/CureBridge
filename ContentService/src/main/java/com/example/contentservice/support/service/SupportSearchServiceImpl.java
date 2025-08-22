package com.example.contentservice.support.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.support.document.SupportDocument;
import com.example.contentservice.support.dto.PagingSupportResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportSearchServiceImpl implements SupportSearchService {


  private final ElasticsearchClient elasticsearchClient;
  private final SupportRepository supportRepository;

  // 제목 으로 검색하는 쿼리를 포함한 메서드
  @Override
  public PagingDto<PagingSupportResponseDto> searchByTitle(String keyword, Pageable pageable) {
    String fieldSuffix = containsKorean(keyword) ? "korean" : "english";
    String ngramField = "ngram";
    int from = pageable.getPageNumber() * pageable.getPageSize();
    try {
      SearchResponse<SupportDocument> response = elasticsearchClient.search(s -> s
              .index("support-index")
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
                      .field("createdAt")
                      .order(SortOrder.Desc)
                  )
              )
              .from(from)
              .size(pageable.getPageSize()),
          SupportDocument.class
      );

      List<PagingSupportResponseDto> list = response.hits().hits().stream()
          .map(hit -> {
            Long supportId = Long.valueOf(hit.id()); // hit의 ID를 Support ID로 사용
            Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));
            return PagingSupportResponseDto.fromDocument(hit.source(), support);
          })
          .collect(Collectors.toList());

      // PagingDto에 검색 결과 리스트와 전체 개수를 담아 반환
      long totalCount = response.hits().total().value();
      return new PagingDto<>(list, totalCount);

    } catch (IOException e) {
      throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
    }
  }

  // 제목+내용 으로 검색하는 쿼리를 포함한 메서드
  @Override
  public PagingDto<PagingSupportResponseDto> searchByAll(String keyword, Pageable pageable) {
    String fieldSuffix = containsKorean(keyword) ? "korean" : "english";
    String ngramField = "ngram";
    try {
      SearchResponse<SupportDocument> response = elasticsearchClient.search(s -> s
              .index("support-index")
              .from(pageable.getPageNumber() * pageable.getPageSize())
              .size(pageable.getPageSize())
              .query(q -> q
                  .bool(b -> b
                      .should(sb -> sb.match(m -> m
                          .field("title." + fieldSuffix)
                          .query(keyword)
                      ))
                      .should(sb -> sb.match(m -> m
                          .field("content." + fieldSuffix)
                          .query(keyword)
                      ))
                      .should(sb -> sb.match(m -> m.field("title." + ngramField).query(keyword)))
                      .should(sb -> sb.match(m -> m.field("content." + ngramField).query(keyword)))

                  )
              )
              .sort(sort -> sort
                  .field(f -> f
                      .field("createdAt")
                      .order(SortOrder.Desc)
                  )
              ),
          SupportDocument.class
      );

      List<PagingSupportResponseDto> list = response.hits().hits().stream()
          .map(hit -> {
            Long supportId = Long.valueOf(hit.id()); // hit의 ID를 Support ID로 사용
            Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));
            return PagingSupportResponseDto.fromDocument(hit.source(), support);
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
}