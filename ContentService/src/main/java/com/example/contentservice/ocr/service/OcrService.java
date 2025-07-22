package com.example.contentservice.ocr.service;

import com.example.contentservice.ocr.dto.UploadRequestDto;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

  String analyzeImageWithClovaOcr(MultipartFile file);

}
