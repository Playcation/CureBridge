package com.example.contentservice.ocr.repository;

import com.example.contentservice.ocr.entity.OcrEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrRepository extends MongoRepository<OcrEntity, String> {

  default OcrEntity findByIdOrElseThrow(String id){
    return findById(id).orElseThrow(()->new IllegalArgumentException("Not found"));
  }

  List<OcrEntity> findByUserId(Long userId);

  void deleteById(String id);
}
