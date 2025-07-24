package com.example.contentservice.ocr.service;

import com.example.contentservice.ocr.dto.DeleteRequestDto;
import com.example.contentservice.ocr.dto.OcrMultiResponseDto;
import com.example.contentservice.ocr.dto.OcrResponseDto;
import com.example.contentservice.ocr.dto.UpdateRequestDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

  OcrResponseDto analyzeImageWithClovaOcr(Long userId, MultipartFile file);

  OcrResponseDto updateOcrResult(Long id, UpdateRequestDto updateRequestDto);

  List<OcrMultiResponseDto> getOcrResult(Long userId);

  OcrResponseDto getOcrDetalResult(Long id);

  String deleteOcrResult(Long id, DeleteRequestDto deleteRequestDto);
}
