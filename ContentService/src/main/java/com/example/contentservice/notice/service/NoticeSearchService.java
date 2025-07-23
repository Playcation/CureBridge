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
public class NoticeSearchService {

	private final ElasticsearchClient elasticsearchClient;

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