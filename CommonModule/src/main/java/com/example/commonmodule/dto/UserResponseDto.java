package com.example.commonmodule.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class UserResponseDto {

	private final String name;
	private final String email;
	private final LocalDate birthDate;
	private final String sick;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;
	private final LocalDateTime deletedAt;
}
