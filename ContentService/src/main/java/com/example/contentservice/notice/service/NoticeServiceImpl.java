package com.example.contentservice.notice.service;

import com.example.commonmodule.common.PagingDto;
import com.example.commonmodule.dto.UserResponseDto;
import com.example.commonmodule.files.dto.FileResponseDto;
import com.example.commonmodule.files.entity.BoardFile;
import com.example.commonmodule.files.entity.BoardFileType;
import com.example.commonmodule.files.repository.BoardFileRepository;
import com.example.commonmodule.files.repository.FileRepository;
import com.example.commonmodule.files.service.FileService;
import com.example.contentservice.config.UserClient;
import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import com.example.contentservice.notice.entity.Notice;
import com.example.contentservice.notice.repository.NoticeRepository;
import com.example.contentservice.notice.repository.NoticeSearchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

  private final NoticeRepository noticeRepository;
  private final NoticeSearchRepository noticeSearchRepository;
  private final FileService fileService;
  private final BoardFileRepository boardFileRepository;
  private final FileRepository fileRepository;
  private final UserClient userClient;

  @Override
  @Transactional
  public NoticeResponseDto createNotice(Long userId, NoticeRequestDto requestDto,
      List<MultipartFile> attachedFiles,
      List<MultipartFile> contentImages) {
    String writerName;

    Notice notice = Notice.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .orgId(0L)
        .viewCount(0L)
        .userId(userId)
        .build();
    Notice savedNotice = noticeRepository.save(notice);

    List<BoardFile> noticeFileList = new ArrayList<>();

    log.info("contentImages: {}", contentImages);

    List<String> contentImagePaths = new ArrayList<>();
    List<String> attachedFilePaths = new ArrayList<>();
    /**
     *     본문 이미지 업로드 및 NoticeFile 저장
     */
    if (contentImages != null && !contentImages.isEmpty()) {
      // 본문 이미지 업로드
      List<FileResponseDto> uploadedContentImages = fileService.uploadFiles(contentImages).join();
      log.info("uploadedContentImages: {}", uploadedContentImages);

      //업로드된 이미지 URL로 NoticeFile 생성
      List<BoardFile> contentImageFiles = uploadedContentImages.stream()
          .map(file -> BoardFile.builder()
              .boardId(savedNotice.getId())
              .fileDetailId(file.getFileId())
              .fileType(BoardFileType.CONTENT_IMAGE_FILE)
              .build())
          .toList();

      for (FileResponseDto file : uploadedContentImages) {
        contentImagePaths.add(file.getFilePath());
      }

      noticeFileList.addAll(contentImageFiles);

      //업로드된 이미지 URL을 content에 반영
      String updatedContent = replaceImageTagsWithUrls(requestDto.getContent(),
          uploadedContentImages);
      savedNotice.update(savedNotice.getTitle(), updatedContent);

      log.info("본문 이미지 업로드 완료: {}개", contentImageFiles.size());
    }

    /**
     * 첨부파일 업로드 및 NoticeFile 저장
     */
    if (attachedFiles != null && !attachedFiles.isEmpty()) {
      List<FileResponseDto> uploadedFiles = fileService.uploadFiles(attachedFiles).join();

      List<BoardFile> attachmentFiles = uploadedFiles.stream()
          .map(file -> BoardFile.builder()
              .boardId(savedNotice.getId())
              .fileDetailId(file.getFileId())
              .fileType(BoardFileType.ATTACHED_FILE)  // 첨부파일 타입
              .build())
          .toList();

      for (FileResponseDto file : uploadedFiles) {
        attachedFilePaths.add(file.getFilePath());
      }
      noticeFileList.addAll(attachmentFiles);
      log.info("첨부파일 업로드 완료: {}개", attachmentFiles.size());
    }

    // 4. NoticeFile 일괄 저장
    if (!noticeFileList.isEmpty()) {
      boardFileRepository.saveAll(noticeFileList);
    }

    noticeSearchRepository.save(
        NoticeDocument.fromEntity(savedNotice)
    );

    try {
      UserResponseDto userInfo = userClient.getUserInfoById(savedNotice.getUserId());
      writerName = userInfo.getName();
    } catch (Exception e) {
      log.warn("작성자 이름 조회 실패. userId={}", savedNotice.getUserId(), e);
      writerName = "작성자";
    }

    return NoticeResponseDto.toDto(savedNotice, writerName, contentImagePaths, attachedFilePaths);
  }

  /**
   * 업로드된 이미지 URL로 content 내 img 태그 src 교체
   */
  private String replaceImageTagsWithUrls(String originalContent,
      List<FileResponseDto> uploadedFiles) {
    Document doc = Jsoup.parse(originalContent);
    Elements images = doc.select("img");

    for (int i = 0; i < images.size() && i < uploadedFiles.size(); i++) {
      images.get(i).attr("src", uploadedFiles.get(i).getFilePath());
    }
    return doc.body().html();
  }

  @Override
  public NoticeResponseDto getNotice(Long noticeId) {
    String writerName;
    Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
    notice.incrementViewCount();
    noticeRepository.save(notice);
    List<BoardFile> boardFiles = boardFileRepository.findByBoardId(noticeId);
    List<String> contentImagePaths = boardFiles.stream()
        .filter(boardFile -> boardFile.getFileType() == BoardFileType.CONTENT_IMAGE_FILE)
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();
    List<String> attachedFilePaths = boardFiles.stream()
        .filter(boardFile -> boardFile.getFileType() == BoardFileType.ATTACHED_FILE)
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();
    try {
      UserResponseDto userInfo = userClient.getUserInfoById(notice.getUserId());
      writerName = userInfo.getName();
    } catch (Exception e) {
      log.warn("공지 상세 작성자 이름 조회 실패. userId={}", notice.getUserId(), e);
      writerName = "작성자";
    }

    return NoticeResponseDto.toDto(notice, writerName, contentImagePaths, attachedFilePaths);
  }

  @Override
  public PagingDto<PagingNoticeResponseDto> getNoticesAndPaging(Pageable pageable) {
    //Pageable pageable = PageRequest.of(page, s, Sort.by(Sort.Direction.DESC, "id"));
    Page<Notice> noticePage = noticeRepository.findAll(pageable);

    List<PagingNoticeResponseDto> noticeDtoList = noticePage.getContent().stream()
        .map(notice -> PagingNoticeResponseDto.toDto(notice))
        .toList();

    return new PagingDto<>(noticeDtoList, noticePage.getTotalElements());
  }

  @Override
  @Transactional
  public NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto requestDto) {
    Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
    notice.update(requestDto.getTitle(), requestDto.getContent());
    Notice updatedNotice = noticeRepository.save(notice);
    noticeSearchRepository.save(
        NoticeDocument.fromEntity(updatedNotice)
    );
    List<BoardFile> boardFiles = boardFileRepository.findByBoardId(noticeId);
    List<String> contentImagePaths = boardFiles.stream()
        .filter(boardFile -> boardFile.getFileType() == BoardFileType.CONTENT_IMAGE_FILE)
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();
    List<String> attachedFilePaths = boardFiles.stream()
        .filter(boardFile -> boardFile.getFileType() == BoardFileType.ATTACHED_FILE)
        .map(boardFile -> fileRepository.findByIdOrElseThrow(boardFile.getFileDetailId())
            .getFilePath())
        .toList();

    String writerName;
    try {
      UserResponseDto userInfo = userClient.getUserInfoById(updatedNotice.getUserId());
      writerName = userInfo.getName();
    } catch (Exception e) {
      log.warn("공지 수정 후 작성자 이름 조회 실패. userId={}", updatedNotice.getUserId(), e);
      writerName = "작성자";
    }

    return NoticeResponseDto.toDto(updatedNotice, writerName, contentImagePaths, attachedFilePaths);
  }

  @Override
  @Transactional
  public void deleteNotice(Long noticeId) {
    noticeRepository.findByIdOrElseThrow(noticeId);
    noticeRepository.deleteById(noticeId);

    noticeSearchRepository.deleteById(String.valueOf(noticeId));
  }
}