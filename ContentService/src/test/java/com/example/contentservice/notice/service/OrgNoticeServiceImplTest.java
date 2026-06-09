package com.example.contentservice.notice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.notice.dto.PagingNoticeResponseDto;
import com.example.contentservice.notice.entity.Notice;
import com.example.contentservice.notice.repository.NoticeRepository;
import com.example.contentservice.notice.repository.NoticeSearchRepository;

@ExtendWith(MockitoExtension.class)
class OrgNoticeServiceImplTest {

	@Mock
	private NoticeRepository noticeRepository;

	@Mock
	private NoticeSearchRepository noticeSearchRepository;

	@InjectMocks
	private OrgNoticeServiceImpl orgNoticeService;

	@Test
	@DisplayName("조직 공지사항 목록을 페이징 조회한다")
	void getOrgNoticesAndPaging_Success_Test() {
		// Given
		Long orgId = 2L;
		Pageable pageable = PageRequest.of(0, 10);

		Notice notice1 = Notice.builder()
			.id(101L)
			.title("병원 공지1")
			.orgId(orgId)
			.viewCount(0L)
			.build();

		Notice notice2 = Notice.builder()
			.id(102L)
			.title("병원 공지2")
			.orgId(orgId)
			.viewCount(5L)
			.build();

		when(noticeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(notice1, notice2), pageable, 2));

		// When
		PagingDto<PagingNoticeResponseDto> result = orgNoticeService.getOrgNoticesAndPaging(orgId, pageable);

		// Then
		assertNotNull(result);
		assertEquals(2L, result.getCount());
		assertEquals(2, result.getList().size());
		assertEquals(101L, result.getList().get(0).getNoticeId());
		assertEquals("병원 공지1", result.getList().get(0).getTitle());

		verify(noticeRepository, times(1)).findAll(pageable);
	}

	@Test
	@DisplayName("조직 공지사항 삭제 시 MySQL과 Elasticsearch에서 함께 삭제한다")
	void deleteOrgNotice_Success_Test() {
		// Given
		Long orgId = 2L;
		Long noticeId = 50L;

		Notice notice = Notice.builder()
			.id(noticeId)
			.orgId(orgId)
			.title("조직 공지")
			.build();

		when(noticeRepository.findByIdAndOrgIdOrThrow(noticeId, orgId)).thenReturn(notice);

		// When
		orgNoticeService.deleteOrgNotice(orgId, noticeId);

		// Then
		verify(noticeRepository, times(1)).findByIdAndOrgIdOrThrow(noticeId, orgId);
		verify(noticeRepository, times(1)).deleteById(noticeId);
		verify(noticeSearchRepository, times(1)).deleteById(String.valueOf(noticeId));
	}
}