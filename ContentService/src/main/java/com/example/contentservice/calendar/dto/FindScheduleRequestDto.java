package com.example.contentservice.calendar.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class FindScheduleRequestDto {
	private LocalDate date;
}
