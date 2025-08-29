package com.example.commonmodule.files.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.example.commonmodule.exceptions.FileErrorCode;
import com.example.commonmodule.exceptions.InternalServerException;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.entity.FileDetail;
import com.example.commonmodule.files.repository.FileRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  @Value("${cloud.aws.s3.bucket.ocr}")
  private String ocrBucket;

  @Value("${cloud.aws.s3.bucket.board}")
  private String boardBucket;

  private final AmazonS3 s3;
  private final FileRepository fileRepository;

  /**
   * S3에 파일 업로드
   */
  @Override
  @Transactional
  public FileResponseDto uploadFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    String bucket = determineBucket(file);
    String fileName = generateUniqueFileName(file.getOriginalFilename());
    String filePath = uploadToS3(bucket, file, fileName);

    FileDetail fileDetail = FileDetail.builder()
        .bucket(bucket)
        .originFileName(file.getOriginalFilename())
        .serverFileName(fileName)
        .fileSize(file.getSize())
        .fileType(file.getContentType())
        .filePath(filePath)
        .build();

    return FileResponseDto.toDto(fileRepository.save(fileDetail));
  }

  @Override
  public CompletableFuture<List<FileResponseDto>> uploadFiles(List<MultipartFile> files) {
    List<FileResponseDto> uploadedFiles = files.stream()
        .map(this::uploadFile)
        .collect(Collectors.toList());
    return CompletableFuture.completedFuture(uploadedFiles);
  }

  @Override
  public FileResponseDto getSingleFile(String fileId) {
    return FileResponseDto.toDto(fileRepository.findByIdOrElseThrow(Long.parseLong(fileId)));
  }

  /**
   * S3에서 파일 삭제 (트랜잭션 분리)
   */
  @Override
  @Transactional
  public String deleteFile(String fileId) {
    FileDetail fileDetail = fileRepository.findByIdOrElseThrow(Long.parseLong(fileId));
    // S3 삭제
    try {
      s3.deleteObject(
          new DeleteObjectRequest(fileDetail.getBucket(), fileDetail.getServerFileName()));
    } catch (Exception e) {
      throw new InternalServerException(FileErrorCode.FAIL_UPLOAD_FILE);
    }

    // DB 삭제 (트랜잭션 적용)
    deleteFileFromDB(fileDetail);

    return "삭제되었습니다.";
  }

  /**
   * DB에서 파일 정보 삭제 (트랜잭션 적용)
   */
  @Transactional
  protected void deleteFileFromDB(FileDetail fileDetail) {
    fileRepository.delete(fileDetail);
  }

  /**
   * S3에서 파일 다운로드
   */
  private FileResponseDto getObjectByFilePath(String filePath) {
    FileDetail fileDetail = fileRepository.findByFilePathOrElseThrow(filePath);
    String bucket = fileDetail.getBucket();

    try (S3ObjectInputStream inputStream = getS3FileStream(bucket,
        fileDetail.getServerFileName())) {
      return FileResponseDto.toDto(fileDetail);
    } catch (IOException e) {
      throw new InternalServerException(FileErrorCode.NOT_FOUND_FILE);
    }
  }

  /**
   * 파일의 버킷 결정
   */
  /* 여기에 그외는 boardBucket 로 */
  private String determineBucket(MultipartFile file) {
    String fileType = getFileExtension(file.getOriginalFilename());
    return ".zip".equals(fileType) ? ocrBucket : boardBucket;
  }

  /**
   * S3 업로드 실행
   */
  private String uploadToS3(String bucket, MultipartFile file, String fileName) {
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());
    try (InputStream inputStream = file.getInputStream()) {
      s3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
//          .withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      throw new InternalServerException(FileErrorCode.FAIL_UPLOAD_FILE);
    }

    return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + fileName;
  }

  /**
   * S3 파일 스트림 가져오기
   */
  private S3ObjectInputStream getS3FileStream(String bucket, String fileName) {
    S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, fileName));
    return s3Object.getObjectContent();
  }

  /**
   * 난수화된 파일명 생성
   */
  private String generateUniqueFileName(String fileName) {
    return LocalDateTime.now() + "_" + UUID.randomUUID() + getFileExtension(fileName);
  }

  /**
   * 파일 확장자 추출
   */
  private String getFileExtension(String fileName) {
    try {
      return fileName.substring(fileName.lastIndexOf("."));
    } catch (StringIndexOutOfBoundsException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일: " + fileName);
    }
  }
}
