package com.example.commonmodule.files.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetail extends BaseEntityUpdatedAt {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long fileDetailId;

  private String originFileName;

  private String serverFileName;

  private String bucket;

  @Builder.Default
  private String filePath = "";

  private Long fileSize;

  private String fileType;
}
