package com.example.memberservice.dto;

import lombok.Getter;

@Getter
public class UpdateUserResponseDto extends MessageResponseDto {

	public UpdateUserResponseDto(String message) {
		super(message);
	}
}
