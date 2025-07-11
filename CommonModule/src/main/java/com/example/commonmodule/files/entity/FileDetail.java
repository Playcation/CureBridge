package com.example.commonmodule.files.entity;

import com.example.commonmodule.base_entity.BaseEntityUpdatedAt;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDetail extends BaseEntityUpdatedAt {

  private Long fileDetailId;

  private String originFileName;

  private String serverFileName;

  private String bucket;

  private String filePath;

  private Long fileSize;

  private String fileType;

  public FileDetail(String originFileName, String serverFileName, String bucket, String filePath, Long fileSize, String fileType) {
    this.originFileName = originFileName;
    this.serverFileName = serverFileName;
    this.bucket = bucket;
    this.filePath = filePath;
    this.fileSize = fileSize;
    this.fileType = fileType;
  }

}
