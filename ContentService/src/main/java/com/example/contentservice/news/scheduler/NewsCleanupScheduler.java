package com.example.contentservice.news.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.contentservice.news.service.NewsSearchService;
import com.example.contentservice.news.service.NewsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsCleanupScheduler {

	private final NewsService newsService;
	private final NewsSearchService newsSearchService;

	// 매일 새벽 1시에 실행 (초 분 시 일 월 요일)
	@Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
	public void cleanupOldNewsTask() {
		// 15일 이상 된 게시물 삭제
		newsService.cleanupOldNews(15);
		newsSearchService.deleteOldNews(15);
	}
}
