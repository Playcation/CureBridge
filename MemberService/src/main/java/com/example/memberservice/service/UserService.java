package com.example.memberservice.service;

import org.springframework.stereotype.Service;

import com.example.memberservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
}
