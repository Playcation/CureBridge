package com.example.contentservice.news.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.contentservice.news.scheduler.NewsCreateScheduler;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test/news")
@RequiredArgsConstructor
public class NewsTestController {

	private final NewsCreateScheduler newsCreateScheduler;

	@GetMapping("/run-scheduler")
	public String runSchedulerForce() {
		try {
			// 스케줄러 메서드를 강제로 실행
			newsCreateScheduler.newsapi();
			return "스케줄러 강제 실행 성공! Redis와 로그를 확인하세요.";
		} catch (Exception e) {
			return "스케줄러 실행 중 오류 발생: " + e.getMessage();
		}
	}
}