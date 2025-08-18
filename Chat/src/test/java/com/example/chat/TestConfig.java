package com.example.chat;

import com.example.commonmodule.config.S3Config;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

  @Bean
  @Primary
  public S3Config s3Config() {
    return Mockito.mock(S3Config.class);
  }
}
