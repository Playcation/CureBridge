package com.example.contentservice.calendar.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class UpdateScheduleRequestDto {
	private LocalDate date;
	private String title;
	private String content;
}
