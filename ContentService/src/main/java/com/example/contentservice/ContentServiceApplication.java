package com.example.contentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
    "com.example.contentservice",
    "com.example.commonmodule"
})
@EnableJpaRepositories(basePackages = {
    "com.example.contentservice.news.repository",
    "com.example.contentservice.notice.repository",
    "com.example.commonmodule.files.repository",
    "com.example.contentservice.ocr.repository",
    "com.example.contentservice.support.repository"
})
@EntityScan(basePackages = {
    "com.example.contentservice.news.entity",
    "com.example.contentservice.notice.entity",
    "com.example.commonmodule.files.entity",
    "com.example.contentservice.ocr.entity",
    "com.example.contentservice.support.entity"

})
public class ContentServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ContentServiceApplication.class, args);
  }

}
