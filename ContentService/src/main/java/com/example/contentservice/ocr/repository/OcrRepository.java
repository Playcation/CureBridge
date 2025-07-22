package com.example.contentservice.ocr.repository;

import com.example.contentservice.ocr.entity.OcrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrRepository extends JpaRepository<OcrEntity, Long> {

}
