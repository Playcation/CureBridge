package com.example.contentservice.news.scheduler;

import com.example.contentservice.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsCleanupScheduler {

  private final NewsService newsService;

  // 매일 새벽 3시에 실행 (초 분 시 일 월 요일)
  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
  public void cleanupOldNewsTask() {
    // 15일 이상 된 게시물 삭제
    newsService.cleanupOldNews(15);
  }
}
