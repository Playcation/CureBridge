package com.example.contentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableJpaAuditing
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.example.contentservice.ocr.repository")
@EnableJpaRepositories(basePackages = {
		"com.example.contentservice.news.repository",
		"com.example.contentservice.notice.repository",
		"com.example.contentservice.support.repository"
})
@SpringBootApplication(scanBasePackages = {
		"com.example.contentservice",   // 모든 컴포넌트 포함
})
public class ContentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContentServiceApplication.class, args);
	}

}
