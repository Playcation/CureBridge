package com.example.contentservice.calendar.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateScheduleRequestDto {

	private LocalDate date;

	@NotBlank(message = "제목을 비워둘 수 없습니다.")
	@Size(max = 25, message = "제목은 최대 25글자입니다.")
	private String title;

	@NotBlank(message = "내용을 비워둘 수 없습니다.")
	private String content;

	public LocalDate getDate() {
		return this.date != null ? this.date : LocalDate.now();
	}
}
