package com.example.memberservice.dto;

import lombok.Getter;

@Getter
public class UpdateUserResponseDto extends MessageResponseDto {

	private final String sick;

	public UpdateUserResponseDto(String message, String sick) {
		super(message);
		this.sick = sick;
	}
}
