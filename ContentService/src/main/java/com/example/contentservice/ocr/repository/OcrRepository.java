package com.example.contentservice.ocr.repository;

import com.example.contentservice.ocr.entity.OcrEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrRepository extends JpaRepository<OcrEntity, Long> {

  default OcrEntity findByIdOrElseThrow(Long id){
    return findById(id).orElseThrow(()->new IllegalArgumentException("Not found"));
  }

  List<OcrEntity> findByUserId(Long userId);

  void deleteById(Long id);
}
