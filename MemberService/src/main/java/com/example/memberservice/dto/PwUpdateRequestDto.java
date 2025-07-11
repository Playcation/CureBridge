package com.example.memberservice.dto;

import lombok.Getter;

@Getter
public class PwUpdateRequestDto {

	private String currPassword;
	private String newPassword;
}
