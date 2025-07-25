package com.example.chat.repository;

import com.example.chat.entity.Chat;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories(basePackages = "com.example.chat.repository.jpa")
public interface ChatMessageRepositoryJPA extends JpaRepository<Chat, String > {

}
