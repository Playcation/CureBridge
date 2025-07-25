package com.example.contentservice.notice.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.dto.NoticeSearchResponseDto;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeSearchServiceImpl implements NoticeSearchService {

	/* (추가) 검색 페이징 처리 */
	/* (추가) 단어 인식 현재는 '병원'은 인식하고 '병원의'의 병원은 인식 못하는 상태. 개선 필요 */

	private final ElasticsearchClient elasticsearchClient;

	// 제목 으로 검색하는 쿼리를 포함한 메서드
	@Override
	public List<NoticeSearchResponseDto> searchByTitle(String keyword) {
		try {
			SearchResponse<NoticeDocument> response = elasticsearchClient.search(s -> s
					.index("notice-index")
					.query(q -> q
						.match(m -> m
							.field("title")
							.query(keyword)
						)
					)
					.sort(sort -> sort
						.field(f -> f
							.field("createdAt")
							.order(SortOrder.Desc)
						)
					),
				NoticeDocument.class
			);

			return response.hits().hits().stream()
				.map(hit -> NoticeSearchResponseDto.fromDocument(hit.source()))
				.collect(Collectors.toList());

		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
		}
	}

	// 제목+내용 으로 검색하는 쿼리를 포함한 메서드
	@Override
	public List<NoticeSearchResponseDto> searchByAll(String keyword) {
		try {
			SearchResponse<NoticeDocument> response = elasticsearchClient.search(s -> s
					.index("notice-index")
					.query(q -> q
						.bool(b -> b
							.should(sb -> sb.match(m -> m
								.field("title")
								.query(keyword)
							))
							.should(sb -> sb.match(m -> m
								.field("content")
								.query(keyword)
							))
						)
					)
					.sort(sort -> sort
						.field(f -> f
							.field("createdAt")
							.order(SortOrder.Desc)
						)
					),
				NoticeDocument.class
			);

			return response.hits().hits().stream()
				.map(hit -> NoticeSearchResponseDto.fromDocument(hit.source()))
				.collect(Collectors.toList());

		} catch (IOException e) {
			throw new RuntimeException("Elasticsearch 검색 중 오류 발생", e);
		}
	}
}