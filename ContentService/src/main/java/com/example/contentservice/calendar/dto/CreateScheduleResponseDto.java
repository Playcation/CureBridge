package com.example.contentservice.calendar.dto;

import com.example.contentservice.calendar.entity.Schedules;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateScheduleResponseDto {

  private Long id;
  private LocalDate date;
  private String title;

  public CreateScheduleResponseDto(Schedules schedule) {
    this.id = schedule.getId();
    this.date = schedule.getDate();
    this.title = schedule.getTitle();
  }
}
