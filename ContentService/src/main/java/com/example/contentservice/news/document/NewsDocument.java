package com.example.contentservice.news.document;

import com.example.contentservice.news.entity.News;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "news-index-v2", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
public class NewsDocument {

  @Id
  private String id;
  private String title;

  @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
  private LocalDateTime publishedAt;

  // 뉴스 제목의 형태소 분석된 토큰을 저장할 필드
  @Field(type = FieldType.Keyword) // Keyword 타입은 정렬 및 애그리게이션에 적합
  private List<String> combinedTokens;


  public static NewsDocument fromEntity(News news) {
    List<String> tokens = Arrays.asList(news.getTitle().split(" "));

    return NewsDocument.builder()
        .id(String.valueOf(news.getId()))
        .title(news.getTitle())
        .publishedAt(news.getPublishedAt())
        .combinedTokens(tokens) //  토큰 데이터 삽입
        .build();
  }
}