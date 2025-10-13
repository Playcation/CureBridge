package com.example.memberservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EntityScan(basePackages = {
	"com.example.memberservice",         // 기존 엔티티
	"com.example.commonmodule.files.entity"
})
@EnableJpaRepositories(basePackages = {
	"com.example.memberservice.repository",
	"com.example.commonmodule.files.repository"
})
@SpringBootApplication(scanBasePackages = {
	"com.example.memberservice"   // 모든 컴포넌트 포함
})
@ComponentScan(basePackages = {
	"com.example.memberservice",
	"com.example.commonmodule"
})
// @SpringBootApplication
public class MemberServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberServiceApplication.class, args);
	}

}
