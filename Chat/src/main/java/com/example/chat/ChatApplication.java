package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = {
    "com.example.chat.repository",
    "com.example.commonmodule.files.repository"
})
@EntityScan(basePackages = {
    "com.example.commonmodule.files.entity",
    "com.example.chat.domain"
})
@EnableFeignClients
@EnableCaching
public class ChatApplication {

  public static void main(String[] args) {
    System.setProperty("application-chat", "common-application");
    SpringApplication.run(ChatApplication.class, args);
  }

}
