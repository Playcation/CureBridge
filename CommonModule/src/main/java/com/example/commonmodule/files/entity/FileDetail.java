package com.example.commonmodule.files.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDetail extends BaseEntityUpdatedAt {

  private Long fileDetailId;

  private String originFileName;

  private String serverFileName;

  private String bucket;

  private String filePath;

  private Long fileSize;

  private String fileType;
}
