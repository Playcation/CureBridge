package com.example.memberservice.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponseDto {

	private final String name;
	private final String email;
	private final Date birthDate;
	private final String sick;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
}
