package com.example.contentservice.news.repository;

import com.example.commonmodule.exceptions.BoardErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.news.entity.News;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
	boolean existsByLink(String link); // 중복 방지

	default News findByIdOrElseThrow(Long id) {
		News news = findById(id).orElseThrow(() -> new NotFoundException(BoardErrorCode.NOT_FOUND_BOARD));

		return news;
	}

	Page<News> findAll(Pageable pageable);

  void deleteByPublishedAtBefore(LocalDateTime dateTime);
}