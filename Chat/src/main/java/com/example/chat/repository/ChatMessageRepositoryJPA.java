package com.example.chat.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.chat.repository.jpa")
public class ChatMessageRepositoryJPA {

}
