package com.example.memberservice.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponseDto {

	private final String name;
	private final String email;
	private final String birthDate;
	private final String sick;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
}
