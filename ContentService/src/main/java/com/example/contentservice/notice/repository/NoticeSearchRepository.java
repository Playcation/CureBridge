package com.example.contentservice.notice.repository;

import com.example.contentservice.notice.document.NoticeDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeSearchRepository extends ElasticsearchRepository<NoticeDocument, String> {

  // 제목 기반 검색
  List<NoticeDocument> findByTitleContaining(String keyword);

  // 제목+내용 전체 검색
  List<NoticeDocument> findByTitleContainingOrContentContaining(String title, String content);
}
