package com.example.contentservice.support.repository;

import com.example.contentservice.support.document.SupportDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportSearchRepository extends ElasticsearchRepository<SupportDocument, String> {

  // 제목 기반 검색
  List<SupportDocument> findByTitleContaining(String keyword);

  // 제목+내용 전체 검색
  List<SupportDocument> findByTitleContainingOrContentContaining(String title, String content);
}
