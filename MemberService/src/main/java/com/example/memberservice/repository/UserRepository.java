package com.example.memberservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.memberservice.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
