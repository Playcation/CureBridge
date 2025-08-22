package com.example.contentservice.support.document;


import com.example.contentservice.support.entity.Support;
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
@Document(indexName = "support-index", createIndex = false, writeTypeHint = WriteTypeHint.FALSE)
public class SupportDocument {

  @Id
  private String id;
  private String title;
  private String content;

  @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS||epoch_millis")
  private LocalDateTime createdAt;

  public static com.example.contentservice.support.document.SupportDocument fromEntity(
      Support support) {
    return com.example.contentservice.support.document.SupportDocument.builder()
        .id(String.valueOf(support.getId())) // Long → String
        .title(support.getTitle())
        .content(support.getContent())
        .createdAt(support.getCreatedAt())
        .build();
  }

}