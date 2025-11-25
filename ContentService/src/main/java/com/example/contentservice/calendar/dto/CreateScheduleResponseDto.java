package com.example.contentservice.calendar.dto;

import java.time.LocalDate;

import com.example.contentservice.calendar.entity.Schedules;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateScheduleResponseDto {
	private final Long id;
	private final LocalDate date;
	private final String title;

	public CreateScheduleResponseDto(Schedules schedule) {
		this.id = schedule.getId();
		this.date = schedule.getDate();
		this.title = schedule.getTitle();
	}
}
