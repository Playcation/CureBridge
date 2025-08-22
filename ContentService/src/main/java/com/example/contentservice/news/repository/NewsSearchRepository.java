package com.example.contentservice.news.repository;

import com.example.contentservice.news.document.NewsDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsSearchRepository extends ElasticsearchRepository<NewsDocument, String> {

  // 제목 기반 검색
  List<NewsDocument> findByTitleContaining(String keyword);

}
