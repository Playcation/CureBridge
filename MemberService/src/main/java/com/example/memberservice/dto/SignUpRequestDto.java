package com.example.memberservice.dto;

import java.util.Date;

import lombok.Getter;

@Getter
public class SignUpRequestDto {

	private String name;
	private String email;
	private String password;
	private String phoneNumber;
	private Date birthDate;
}
