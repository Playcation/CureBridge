package com.example.contentservice.calendar.service;

import com.example.commonmodule.exceptions.NoAuthorizedException;
import com.example.contentservice.calendar.dto.CreateScheduleRequestDto;
import com.example.contentservice.calendar.dto.CreateScheduleResponseDto;
import com.example.contentservice.calendar.dto.FindScheduleRequestDto;
import com.example.contentservice.calendar.dto.ScheduleResponseDto;
import com.example.contentservice.calendar.dto.UpdateScheduleRequestDto;
import com.example.contentservice.calendar.entity.Schedules;
import com.example.contentservice.calendar.repository.CalendarRepository;
import com.example.contentservice.exceptions.CalendarException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

  private final CalendarRepository calendarRepository;
  private final CacheManager cacheManager;

  @CacheEvict(value = "calendar", key = "'monthly:' + #orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue()")
  @Transactional
  public CreateScheduleResponseDto createSchedule(Long orgId, CreateScheduleRequestDto requestDto) {
    Schedules schedule = Schedules.builder()
        .orgId(orgId)
        .date(requestDto.getDate())
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .build();

    Schedules savedSchedule = calendarRepository.save(schedule);
    return new CreateScheduleResponseDto(savedSchedule);
  }

  @Cacheable(value = "calendar", key = "'monthly:' + #orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue()")
  public List<ScheduleResponseDto> findAllSchedulesByMonth(Long orgId,
      FindScheduleRequestDto requestDto) {
    LocalDate curr = requestDto.getDate();
    LocalDate start = curr.withDayOfMonth(1);
    LocalDate end = curr.withDayOfMonth(curr.lengthOfMonth());

    List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId, start, end);

    return schedules.stream().map(ScheduleResponseDto::new).toList();
  }

  public List<ScheduleResponseDto> findAllSchedulesByDate(Long orgId,
      FindScheduleRequestDto requestDto) {
    List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId,
        requestDto.getDate(),
        requestDto.getDate());

    return schedules.stream().map(ScheduleResponseDto::new).toList();
  }

  @Transactional
  public ScheduleResponseDto updateSchedule(Long id, Long orgId,
      UpdateScheduleRequestDto requestDto) {
    Schedules findSchedule = calendarRepository.findByIdOrElseThrow(id);
    if (!findSchedule.getOrgId().equals(orgId)) {
      throw new NoAuthorizedException(CalendarException.NO_AUTHORIZATION);
    }

    // 변경 전 기존 일정의 캐시 키 생성
    String oldCacheKey =
        "monthly:" + orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate()
            .getMonthValue();
    // 변경 후 새로운 일정의 캐시 키 생성
    String newCacheKey =
        "monthly:" + orgId + ":" + requestDto.getDate().getYear() + "-" + requestDto.getDate()
            .getMonthValue();

    findSchedule.update(requestDto.getDate(), requestDto.getTitle(), requestDto.getContent());

    // 기존 월과 새로운 월의 캐시를 모두 무효화
    if (cacheManager.getCache("calendar") != null) {
      cacheManager.getCache("calendar").evict(oldCacheKey);
      if (!oldCacheKey.equals(newCacheKey)) {
        cacheManager.getCache("calendar").evict(newCacheKey);
      }
    }
    return new ScheduleResponseDto(findSchedule);
  }

  @Transactional
  public void deleteSchedule(Long id, Long orgId) {
    Schedules findSchedule = calendarRepository.findByIdOrElseThrow(id);
    if (!findSchedule.getOrgId().equals(orgId)) {
      throw new NoAuthorizedException(CalendarException.NO_AUTHORIZATION);
    }

    // 삭제할 일정의 날짜 기반으로 캐시 키 생성
    String cacheKey =
        "monthly:" + orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate()
            .getMonthValue();

    calendarRepository.delete(findSchedule);

    // 수동으로 캐시 삭제
    if (cacheManager.getCache("calendar") != null) {
      cacheManager.getCache("calendar").evict(cacheKey);
    }
  }
}
