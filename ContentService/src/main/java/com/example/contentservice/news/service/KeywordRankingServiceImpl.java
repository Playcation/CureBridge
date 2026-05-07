package com.example.contentservice.news.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service("keywordRankingService")
@RequiredArgsConstructor
public class KeywordRankingServiceImpl implements KeywordRankingService {

	private final RedisTemplate<String, Object> redisTemplate;
	private static final String COUNT_KEY_PREFIX = "keyword_count::";

	public long incrementSearchCount(String keyword) {
		// Redis의 INCR 명령어를 실행
		Long count = redisTemplate.opsForValue().increment(COUNT_KEY_PREFIX + keyword);
		return count != null ? count : 0L;
	}

	public long getSearchCount(String keyword) {
		Object count = redisTemplate.opsForValue().get(COUNT_KEY_PREFIX + keyword);

		if (count instanceof Integer) {
			return ((Integer)count).longValue();
		} else if (count instanceof Long) {
			return (Long)count;
		} else if (count instanceof String) {
			return Long.parseLong((String)count);
		}

		return 0L;
	}
}