package com.example.contentservice.calendar.controller;

import com.example.commonmodule.config.TokenSettings;
import com.example.commonmodule.utils.JwtParser;
import com.example.contentservice.calendar.dto.CreateScheduleRequestDto;
import com.example.contentservice.calendar.dto.CreateScheduleResponseDto;
import com.example.contentservice.calendar.dto.FindScheduleRequestDto;
import com.example.contentservice.calendar.dto.ScheduleResponseDto;
import com.example.contentservice.calendar.dto.UpdateScheduleRequestDto;
import com.example.contentservice.calendar.service.CalendarService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

  private final CalendarService calendarService;
  private final JwtParser jwtParser;

  @PostMapping
  public ResponseEntity<CreateScheduleResponseDto> createSchedule(
      @RequestBody @Valid CreateScheduleRequestDto requestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    CreateScheduleResponseDto responseDto = calendarService.createSchedule(
        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);

    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/monthly")
  public ResponseEntity<List<ScheduleResponseDto>> findAllSchedules(
      @RequestBody FindScheduleRequestDto requestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    List<ScheduleResponseDto> schedules = calendarService.findAllSchedulesByMonth(
        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);

    return new ResponseEntity<>(schedules, HttpStatus.OK);
  }

  @GetMapping("/day")
  public ResponseEntity<?> findScheduleByDate(
      @RequestBody FindScheduleRequestDto requestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    List<ScheduleResponseDto> schedules = calendarService.findAllSchedulesByDate(
        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);

    return new ResponseEntity<>(schedules, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateSchedule(
      @PathVariable Long id,
      @RequestBody UpdateScheduleRequestDto requestDto,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    ScheduleResponseDto scheduleResponseDto = calendarService.updateSchedule(id,
        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);

    return new ResponseEntity<>(scheduleResponseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteSchedule(
      @PathVariable Long id,
      @RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
  ) {
    calendarService.deleteSchedule(id, jwtParser.findOrgIdByToken(authorizationHeader));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
