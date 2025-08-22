package com.example.memberservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 단일 message만 전달하는 dto
@Getter
@RequiredArgsConstructor
public class MessageResponseDto {
	private final String message;
}
