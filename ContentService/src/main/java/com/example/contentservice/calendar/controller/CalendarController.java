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
import org.springframework.web.bind.annotation.ModelAttribute;
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
      @ModelAttribute FindScheduleRequestDto requestDto,
      //@RequestHeader(TokenSettings.ACCESS_TOKEN_CATEGORY) String authorizationHeader
      @RequestHeader("Authorization") String authorizationHeader
  ) {
    List<ScheduleResponseDto> schedules = calendarService.findAllSchedulesByMonth(
        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);

    return new ResponseEntity<>(schedules, HttpStatus.OK);
  }
//  @GetMapping("/monthly")
//  public ResponseEntity<?> findAllSchedules(
//      // 1. DTO 대신 String으로 직접 받아서 날짜 형식이 문제인지 확인
//      @RequestParam(value = "date", required = false) String date,
//
//      // 2. 헤더가 없어서 튕기는지 확인하기 위해 required = false 설정
//      @RequestHeader(value = "Authorization", required = false) String authorizationHeader
//  ) {
//    // 3. 콘솔에 로그 강제 출력 (이게 찍히면 컨트롤러 진입 성공)
//    System.out.println(">>> [DEBUG] /monthly 요청 도착!");
//    System.out.println(">>> [DEBUG] date 파라미터: " + date);
//    System.out.println(">>> [DEBUG] Authorization 헤더: " + authorizationHeader);
//
//    if (authorizationHeader == null) {
//      System.out.println(">>> [ERROR] 헤더가 널입니다!");
//      return ResponseEntity.badRequest().body("Authorization Header is missing");
//    }
//
//    if (date == null) {
//      System.out.println(">>> [ERROR] 날짜가 널입니다!");
//      return ResponseEntity.badRequest().body("Date parameter is missing");
//    }
//
//    // 4. 수동으로 DTO 변환 및 로직 실행
//    FindScheduleRequestDto requestDto = new FindScheduleRequestDto();
//    requestDto.setDate(java.time.LocalDate.parse(date)); // 날짜 파싱 테스트
//
//    List<ScheduleResponseDto> schedules = calendarService.findAllSchedulesByMonth(
//        jwtParser.findOrgIdByToken(authorizationHeader), requestDto);
//
//    return new ResponseEntity<>(schedules, HttpStatus.OK);
//  }

  @GetMapping("/day")
  public ResponseEntity<?> findScheduleByDate(
      @ModelAttribute FindScheduleRequestDto requestDto,
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
