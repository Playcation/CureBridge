package com.example.contentservice;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@EnableJpaAuditing
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = {
	"com.example.contentservice.ocr.repository",
	"com.example.contentservice.report.repository"
})
@EntityScan(basePackages = {
	"com.example.contentservice",         // 기존 엔티티
	"com.example.commonmodule.files.entity"  // ✅ 추가된 경로
})
@EnableJpaRepositories(basePackages = {
	"com.example.contentservice.news.repository",
	"com.example.contentservice.notice.repository",
	"com.example.contentservice.support.repository",
	"com.example.contentservice.calendar.repository",
	"com.example.commonmodule.files.repository"
})
@SpringBootApplication(scanBasePackages = {
	"com.example.contentservice"   // 모든 컴포넌트 포함
})
@ComponentScan(basePackages = {
	"com.example.contentservice",
	"com.example.commonmodule"
})
@EnableScheduling
@EnableFeignClients(basePackages = "com.example")
public class ContentServiceApplication {
	@PostConstruct
	public void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ContentServiceApplication.class, args);
	}

}
