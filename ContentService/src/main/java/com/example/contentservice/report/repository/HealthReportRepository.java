package com.example.contentservice.report.repository;

import com.example.commonmodule.exceptions.NotFoundException;
import com.example.contentservice.exceptions.HealthReportException;
import com.example.contentservice.exceptions.OcrException;
import com.example.contentservice.report.entity.HealthReport;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthReportRepository extends MongoRepository<HealthReport, String> {

  List<HealthReport> findByUserId(Long userId);

  Optional<HealthReport> findById(String id);
  default HealthReport findByIdOrElseThrow(String id){
    return this.findById(id).orElseThrow(()->new NotFoundException(HealthReportException.NOT_FOUND_HEALTH_REPORT));
  }

  void deleteById(String id);
  default void deleteByIdOrElseThrow(String id){
    try{
      deleteById(id);
    }catch(Exception e){
      throw new NotFoundException(OcrException.NOT_FOUND_OCR_RESULT);
    }
  }
}
