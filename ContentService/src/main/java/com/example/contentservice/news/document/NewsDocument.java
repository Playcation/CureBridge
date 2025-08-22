package com.example.contentservice.news.document;

import com.example.contentservice.news.entity.News;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
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
@Document(indexName = "news-index", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
public class NewsDocument {

  @Id
  private String id;
  private String title;

  @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
  private LocalDateTime publishedAt;

  public static com.example.contentservice.news.document.NewsDocument fromEntity(News news) {
    return com.example.contentservice.news.document.NewsDocument.builder()
        .id(String.valueOf(news.getId())) // Long → String
        .title(news.getTitle())
        .publishedAt(news.getPublishedAt())
        .build();
  }

}