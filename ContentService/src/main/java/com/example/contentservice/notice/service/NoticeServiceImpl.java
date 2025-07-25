package com.example.contentservice.notice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.document.NoticeDocument;
import com.example.contentservice.notice.dto.NoticeRequestDto;
import com.example.contentservice.notice.dto.NoticeResponseDto;
import com.example.contentservice.notice.entity.Notice;
import com.example.contentservice.notice.repository.NoticeRepository;
import com.example.contentservice.notice.repository.NoticeSearchRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

	private final NoticeRepository noticeRepository;
	private final NoticeSearchRepository noticeSearchRepository;
	private final ElasticsearchTemplate elasticsearchTemplate;

	@Transactional
	public NoticeResponseDto createNotice(NoticeRequestDto requestDto, Long userId) {

		Notice notice = Notice.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.userId(userId)
			.build();
		Notice savedNotice = noticeRepository.save(notice);

		noticeSearchRepository.save(
			NoticeDocument.fromEntity(savedNotice)
		);

		return NoticeResponseDto.toDto(notice);
	}

	public NoticeResponseDto getNotice(Long noticeId) {
		Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
		return NoticeResponseDto.toDto(notice);
	}

	/* (추가) 페이징 미완성 상태. 개선 필요 */
	public PagingDto<NoticeResponseDto> getNoticesAndPaging(int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
		Page<Notice> noticePage = noticeRepository.findAll(pageable);

		List<NoticeResponseDto> noticeDtoList = noticePage.getContent().stream()
			.map(NoticeResponseDto::toDto)
			.toList();

		return new PagingDto<>(noticeDtoList, noticePage.getTotalElements());
	}

	@Transactional
	public NoticeResponseDto updateNotice(Long noticeId, NoticeRequestDto requestDto) {
		Notice notice = noticeRepository.findByIdOrElseThrow(noticeId);
		notice.update(requestDto.getTitle(), requestDto.getContent());
		Notice updatedNotice = noticeRepository.save(notice);
		noticeSearchRepository.save(
			NoticeDocument.fromEntity(updatedNotice)
		);
		return NoticeResponseDto.toDto(notice);
	}

	@Transactional
	public void deleteNotice(Long noticeId) {
		noticeRepository.findByIdOrElseThrow(noticeId);
		noticeRepository.deleteById(noticeId);

		noticeSearchRepository.deleteById(String.valueOf(noticeId));
	}
}