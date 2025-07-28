package com.example.contentservice.ocr.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ocr")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
public class OcrEntity {

  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String ocrId;

  private Long userId;

  private String reportTitle;

  private String reportDate;

  private String patientName;

  private String diagnosis;

  private List<String> rawText;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  public void updateOcr(UpdateRequestDto updateRequestDto){
    this.reportTitle = updateRequestDto.getReportTitle();
    this.reportDate = updateRequestDto.getReportDate();
    this.patientName = updateRequestDto.getPatientName();
    this.diagnosis = updateRequestDto.getDiagnosis();
    this.rawText = updateRequestDto.getRawText();
  }
}
