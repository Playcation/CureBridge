package com.example.chat;

import com.example.commonmodule.config.RedisTemplateConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {
    "com.example.chat",
    "com.example.commonmodule"
})
@EnableJpaRepositories(basePackages = "com.example.commonmodule")
@EntityScan(basePackages = "com.example.commonmodule")
public class ChatApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatApplication.class, args);
  }

}
