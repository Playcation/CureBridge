package com.example.contentservice.calendar.dto;

import com.example.contentservice.calendar.entity.Schedules;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleResponseDto {

  private final Long id;
  private final LocalDate date;
  private final String title;
  private final String content;

  public ScheduleResponseDto(Schedules schedules) {
    this.id = schedules.getId();
    this.date = schedules.getDate();
    this.title = schedules.getTitle();
    this.content = schedules.getContent();
  }
}
