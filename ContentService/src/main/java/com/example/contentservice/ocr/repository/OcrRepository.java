package com.example.contentservice.ocr.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.exceptions.OcrException;
import com.example.contentservice.ocr.entity.OcrEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrRepository extends MongoRepository<OcrEntity, String> {

  default OcrEntity findByIdOrElseThrow(String id) {
    return findById(id).orElseThrow(() -> new NotFoundException(OcrException.NOT_FOUND_OCR_RESULT));
  }

  List<OcrEntity> findByUserId(Long userId);

  void deleteById(String id);

  default void deleteByIdOrElseThrow(String id) {
    try {
      deleteById(id);
    } catch (Exception e) {
      throw new NotFoundException(OcrException.NOT_FOUND_OCR_RESULT);
    }
  }

  List<OcrEntity> findByUserIdAndReportDateBetween(Long userId, LocalDate reportDate,
      LocalDate reportDate2);

  List<OcrEntity> findByReportDateBetween(LocalDate start, LocalDate end);
}
