package com.example.contentservice.calendar.dto;

import com.example.contentservice.calendar.entity.Schedules;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleResponseDto {

  private Long id;
  private LocalDate date;
  private String title;
  private String content;

  public ScheduleResponseDto(Schedules schedules) {
    this.id = schedules.getId();
    this.date = schedules.getDate();
    this.title = schedules.getTitle();
    this.content = schedules.getContent();
  }
}
