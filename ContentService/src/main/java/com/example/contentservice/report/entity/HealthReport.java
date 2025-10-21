package com.example.contentservice.report.entity;

import com.example.contentservice.report.dto.UpdateHealthReportRequestDto;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "healthReport")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthReport {

  @Id
  private String id;

  private Long userId;

  private String title;

  private LocalDate reportDate;

  private String summary;

  private Integer rate;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void updateHealthReport(UpdateHealthReportRequestDto updateHealthReportRequestDto) {
    this.title = updateHealthReportRequestDto.getTitle();
    this.reportDate = updateHealthReportRequestDto.getReportDate();
    this.summary = updateHealthReportRequestDto.getSummary();
    this.rate = updateHealthReportRequestDto.getRate();
  }
}
