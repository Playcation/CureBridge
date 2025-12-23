package com.example.contentservice.calendar.dto;

import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

//@Getter
@Data
public class FindScheduleRequestDto {

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;
}
