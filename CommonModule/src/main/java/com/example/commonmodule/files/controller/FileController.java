package com.example.commonmodule.files.controller;

import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/api/files")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload")
  public ResponseEntity<FileResponseDto> uploadSingleFile(
      @RequestPart MultipartFile file
  ){
    // TODO : 단일 파일 업로드
    return null;
  }

  @PostMapping("/multi-upload")
  public ResponseEntity<FileResponseDto> uploadMultiFile(
      @RequestPart MultipartFile[] files
  ){
    // TODO : 다중 파일 업로드
    return null;
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<FileResponseDto> getSingleFile(
      @PathVariable String fileId
  ){
    // TODO : 단일 파일 조회
    return null;
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<String> deleteSingleFile(
      @PathVariable String fileId
  ){
    // TODO : 단일 파일 삭제
    return null;
  }

}
