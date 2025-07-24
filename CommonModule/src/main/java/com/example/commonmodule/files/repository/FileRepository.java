package com.example.commonmodule.files.repository;

import com.example.commonmodule.exceptions.FileErrorCode;
import com.example.commonmodule.exceptions.NotFoundException;
import com.example.commonmodule.files.entity.FileDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileDetail, Long> {

  Optional<FileDetail> findByOriginFileName(String fileName);
  default FileDetail findByFileNameOrElseThrow(String fileName){
    FileDetail fileDetail = findByOriginFileName(fileName).orElseThrow(() -> new NotFoundException(
        FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

  default FileDetail findByIdOrElseThrow(Long id){
    FileDetail fileDetail = findById(id).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

  Optional<FileDetail> findByServerFileName(String fileName);
  default FileDetail findByServerFileNameOrElseThrow(String fileName){
    FileDetail fileDetail = findByServerFileName(fileName).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

  Optional<FileDetail> findByFilePath(String filePath);
  default FileDetail findByFilePathOrElseThrow(String filePath){
    FileDetail fileDetail = findByFilePath(filePath).orElseThrow(() -> new NotFoundException(FileErrorCode.NOT_FOUND_FILE));
    return fileDetail;
  }

}
