package com.example.memberservice.service;

import org.springframework.stereotype.Service;

import com.example.memberservice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
}
