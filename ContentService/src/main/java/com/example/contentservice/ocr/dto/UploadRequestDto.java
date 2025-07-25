package com.example.contentservice.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class UploadRequestDto {
  private String version;
  private String requestId;
  private long timestamp;
  private String lang;
  private List<ImageData> images;

}
