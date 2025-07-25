package com.example.commonmodule.files.service;

import com.example.commonmodule.files.dto.FileResponseDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  FileResponseDto uploadFile(MultipartFile file);

  CompletableFuture<List<FileResponseDto>> uploadFiles(List<MultipartFile> files);

  FileResponseDto getSingleFile(String fileId);

  String deleteFile(String fileId);
}
