package com.example.memberservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PwUpdateRequestDto {

	private String currPassword;
	private String newPassword;
}
