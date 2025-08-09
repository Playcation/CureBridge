package com.example.contentservice.support.service;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.InvalidInputException;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.entity.BoardFile;
import com.example.commonmodule.files.entity.BoardFileType;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.repository.FileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.support.dto.SupportDetailResponseDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

  private final SupportRepository supportRepository;
  private final BoardFileRepository boardFileRepository;
  private final FileService fileService;
  private final FileRepository fileRepository;

  @Override
  @Transactional
  public SupportResponseDto createSupport(SupportRequestDto requestDto, Long userId,
      List<MultipartFile> attachedFiles) {

    Support support = Support.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .viewCount(0L)
        .userId(userId)
        .build();
    Support savedSupport = supportRepository.save(support);

    /**
     * 첨부파일 업로드 및 NoticeFile 저장
     */
    List<BoardFile> attachmentFiles = new ArrayList<>();
    List<String> attachedFilePaths = new ArrayList<>();

    if (attachedFiles != null && !attachedFiles.isEmpty()) {
      List<FileResponseDto> uploadedFiles = fileService.uploadFiles(attachedFiles).join();

      attachmentFiles = uploadedFiles.stream()
          .map(file -> BoardFile.builder()
              .boardId(savedSupport.getId())
              .fileDetailId(file.getFileId())
              .filePath(file.getFilePath())
              .fileType(BoardFileType.ATTACHED_FILE)  // 첨부파일 타입
              .build())
          .toList();

      for (FileResponseDto file : uploadedFiles) {
        attachedFilePaths.add(file.getFilePath());
      }
    }
    if (!attachmentFiles.isEmpty()) {
      boardFileRepository.saveAll(attachmentFiles);
    }

    return SupportResponseDto.toDto(savedSupport, attachedFilePaths);
  }

  @Override
  @Transactional(readOnly = true)
  public SupportDetailResponseDto getSupport(Long supportId) {
    Support support = supportRepository.findByIdOrElseThrow(supportId);
    support.incrementViewCount();
    supportRepository.save(support);
    List<BoardFile> boardFiles = boardFileRepository.findByBoardId(supportId);
    List<String> attachedFilePaths = boardFiles.stream()
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();
    return SupportDetailResponseDto.toDto(support, attachedFilePaths);
  }

  @Override
  public PagingDto<SupportResponseDto> getSupportsAndPaging(int page) {
    Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
    Page<Support> supportPage = supportRepository.findAll(pageable);

    List<SupportResponseDto> supportDtoList = supportPage.getContent().stream()
        .map(support -> {
          // Support에 연결된 BoardFile 조회
          List<BoardFile> boardFiles = boardFileRepository.findByBoardId(support.getId());

          // fileDetailId를 이용하여 filePath 조회
          List<String> attachedFilePaths = boardFiles.stream()
              .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
                  .getFilePath())
              .toList();

          // filePath 리스트 포함하여 DTO 변환
          return SupportResponseDto.toDto(support, attachedFilePaths);
        })
        .toList();

    return new PagingDto<>(supportDtoList, supportPage.getTotalElements());
  }

  @Override
  @Transactional
  public SupportResponseDto updateSupport(Long supportId, SupportRequestDto requestDto) {
    Support support = supportRepository.findByIdOrElseThrow(supportId);

    // isReplied가 true이면 수정 불가
    if (support.isReplied()) {
      throw new InvalidInputException(BoardErrorCode.REPLIED_SUPPORT);
    }

    support.update(requestDto.getTitle(), requestDto.getContent(), requestDto.isPrivate());
    supportRepository.save(support);

    List<BoardFile> boardFiles = boardFileRepository.findByBoardId(supportId);
    List<String> attachedFilePaths = boardFiles.stream()
        .filter(boardFile -> boardFile.getFileType() == BoardFileType.ATTACHED_FILE)
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();
    return SupportResponseDto.toDto(support, attachedFilePaths);
  }

  @Override
  @Transactional
  public void deleteSupport(Long supportId) {
    Support support = supportRepository.findByIdOrElseThrow(supportId);

    // isReplied가 true이면 수정 불가
    if (support.isReplied()) {
      throw new InvalidInputException(BoardErrorCode.REPLIED_SUPPORT);
    }

    supportRepository.deleteById(supportId);
  }
}
