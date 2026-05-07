package com.example.contentservice.news.service;

public interface KeywordRankingService {
	long incrementSearchCount(String keyword);

	long getSearchCount(String keyword);
}
