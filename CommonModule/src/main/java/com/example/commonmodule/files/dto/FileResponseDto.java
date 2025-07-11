package com.example.commonmodule.files.dto;

import com.example.commonmodule.files.entity.FileDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponseDto {

  private Long fileId;

  private String fileName;

  private String fileType;

  private String url;

  public static FileResponseDto toDto(FileDetail file) {
    return FileResponseDto.builder()
        .fileId(file.getFileDetailId())
        .fileName(file.getOriginFileName())
        .fileType(file.getFilePath())
        .url(file.getFilePath())
        .build();
  }

}
