package com.example.commonmodule.files.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.commonmodule.exceptions.FileErrorCode;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.entity.FileDetail;
import com.example.commonmodule.files.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private AmazonS3 s3;

    @InjectMocks
    private FileServiceImpl fileService;

    private FileDetail fileDetail;
    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        fileDetail = FileDetail.builder()
                .fileDetailId(1L)
                .originFileName("test.jpg")
                .serverFileName("unique_test.jpg")
                .bucket("test-bucket")
                .filePath("https://test-bucket.s3.ap-northeast-2.amazonaws.com/unique_test.jpg")
                .fileSize(1024L)
                .fileType("image/jpeg")
                .build();

        mockMultipartFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        // Inject @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(fileService, "ocrBucket", "ocr-test-bucket");
    }

    @Test
    @DisplayName("단일 파일 업로드 테스트")
    void uploadFile() {
        // Given
        when(s3.putObject(any(PutObjectRequest.class))).thenReturn(null); // S3 putObject returns void or PutObjectResult
        when(fileRepository.save(any(FileDetail.class))).thenReturn(fileDetail);

        // When
        FileResponseDto responseDto = fileService.uploadFile(mockMultipartFile);

        // Then
        assertNotNull(responseDto);
        assertEquals(fileDetail.getOriginFileName(), responseDto.getFileName());
        verify(s3, times(1)).putObject(any(PutObjectRequest.class));
        verify(fileRepository, times(1)).save(any(FileDetail.class));
    }

    @Test
    @DisplayName("다중 파일 업로드 테스트")
    void uploadFiles() {
        // Given
        List<MultipartFile> files = Arrays.asList(mockMultipartFile, mockMultipartFile);
        when(s3.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(fileRepository.save(any(FileDetail.class))).thenReturn(fileDetail);

        // When
        CompletableFuture<List<FileResponseDto>> futureResponse = fileService.uploadFiles(files);
        List<FileResponseDto> responseList = futureResponse.join();

        // Then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        verify(s3, times(2)).putObject(any(PutObjectRequest.class));
        verify(fileRepository, times(2)).save(any(FileDetail.class));
    }

    @Test
    @DisplayName("단일 파일 조회 테스트")
    void getSingleFile() {
        // Given
        when(fileRepository.findByIdOrElseThrow(anyLong())).thenReturn(fileDetail);

        // When
        FileResponseDto responseDto = fileService.getSingleFile("1");

        // Then
        assertNotNull(responseDto);
        assertEquals(fileDetail.getOriginFileName(), responseDto.getFileName());
        verify(fileRepository, times(1)).findByIdOrElseThrow(anyLong());
    }

    @Test
    @DisplayName("파일 삭제 테스트")
    void deleteFile() {
        // Given
        when(fileRepository.findByIdOrElseThrow(anyLong())).thenReturn(fileDetail);
        doNothing().when(s3).deleteObject(any(DeleteObjectRequest.class));
        doNothing().when(fileRepository).delete(any(FileDetail.class));

        // When
        String result = fileService.deleteFile("1");

        // Then
        assertEquals("삭제되었습니다.", result);
        verify(fileRepository, times(1)).findByIdOrElseThrow(anyLong());
        verify(s3, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(fileRepository, times(1)).delete(any(FileDetail.class));
    }

    @Test
    @DisplayName("존재하지 않는 파일 조회 시 NotFoundException 발생 테스트")
    void getSingleFile_NotFound() {
        // Given
        when(fileRepository.findByIdOrElseThrow(anyLong())).thenThrow(new NotFoundException(FileErrorCode.NOT_FOUND_FILE));

        // When & Then
        assertThrows(NotFoundException.class, () -> fileService.getSingleFile("999"));
        verify(fileRepository, times(1)).findByIdOrElseThrow(anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 파일 삭제 시 NotFoundException 발생 테스트")
    void deleteFile_NotFound() {
        // Given
        when(fileRepository.findByIdOrElseThrow(anyLong())).thenThrow(new NotFoundException(FileErrorCode.NOT_FOUND_FILE));

        // When & Then
        assertThrows(NotFoundException.class, () -> fileService.deleteFile("999"));
        verify(fileRepository, times(1)).findByIdOrElseThrow(anyLong());
        verify(s3, never()).deleteObject(any(DeleteObjectRequest.class));
        verify(fileRepository, never()).delete(any(FileDetail.class));
    }
}
