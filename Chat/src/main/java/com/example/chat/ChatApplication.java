package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories
public class ChatApplication {

  public static void main(String[] args) {
    System.setProperty("application-chat", "common-application");
    SpringApplication.run(ChatApplication.class, args);
  }

}
