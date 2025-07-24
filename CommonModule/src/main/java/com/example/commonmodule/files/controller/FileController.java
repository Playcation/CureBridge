package com.example.commonmodule.files.controller;

import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.entity.FileDetail;
import com.example.commonmodule.files.service.FileService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
    FileResponseDto fileDetail = fileService.uploadFile(file);
    return ResponseEntity.ok().body(fileDetail);
  }

  @PostMapping("/multi-upload")
  public ResponseEntity<List<FileResponseDto>> uploadMultiFile(
      @RequestPart List<MultipartFile> files
  ){
    CompletableFuture<List<FileResponseDto>> urls = fileService.uploadFiles(files);
    return ResponseEntity.ok().body(urls.join());
  }

  @GetMapping("/{fileId}")
  public ResponseEntity<FileResponseDto> getSingleFile(
      @PathVariable String fileId
  ){
    return ResponseEntity.ok().body(fileService.getSingleFile(fileId));
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<String> deleteSingleFile(
      @PathVariable String fileId
  ){
    return ResponseEntity.ok().body(fileService.deleteFile(fileId));
  }

}
