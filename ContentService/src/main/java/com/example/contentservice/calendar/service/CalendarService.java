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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

  private final CalendarRepository calendarRepository;
  private final CacheManager cacheManager;

  @Caching(evict = {
      @CacheEvict(value = "calendar_monthly", key = "#orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue()"),
      @CacheEvict(value = "calendar_daily", key = "#orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue() + '-' + #requestDto.date.getDayOfMonth()")
  })  @Transactional
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

  @Cacheable(value = "calendar_monthly", key = "#orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue()")  public List<ScheduleResponseDto> findAllSchedulesByMonth(Long orgId,
      FindScheduleRequestDto requestDto) {
    LocalDate curr = requestDto.getDate();
    LocalDate start = curr.withDayOfMonth(1);
    LocalDate end = curr.withDayOfMonth(curr.lengthOfMonth());

    List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId, start, end);

    return schedules.stream()
        .map(ScheduleResponseDto::new)
        .collect(Collectors.toList());
  }

  @Cacheable(value = "calendar_daily", key = "#orgId + ':' + #requestDto.date.getYear() + '-' + #requestDto.date.getMonthValue() + '-' + #requestDto.date.getDayOfMonth()")  public List<ScheduleResponseDto> findAllSchedulesByDate(Long orgId,
      FindScheduleRequestDto requestDto) {
    List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId,
        requestDto.getDate(),
        requestDto.getDate());

    return schedules.stream()
        .map(ScheduleResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public ScheduleResponseDto updateSchedule(Long id, Long orgId,
      UpdateScheduleRequestDto requestDto) {
    Schedules findSchedule = calendarRepository.findByIdOrElseThrow(id);
    if (!findSchedule.getOrgId().equals(orgId)) {
      throw new NoAuthorizedException(CalendarException.NO_AUTHORIZATION);
    }

    // 수동 캐시 키에서 "calendar_monthly:" 같은 접두사 문자열 제거
    String oldMonthlyCacheKey = orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate().getMonthValue();
    String oldDailyCacheKey = orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate().getMonthValue() + "-" + findSchedule.getDate().getDayOfMonth();

    String newMonthlyCacheKey = orgId + ":" + requestDto.getDate().getYear() + "-" + requestDto.getDate().getMonthValue();
    String newDailyCacheKey = orgId + ":" + requestDto.getDate().getYear() + "-" + requestDto.getDate().getMonthValue() + "-" + requestDto.getDate().getDayOfMonth();

    findSchedule.update(requestDto.getDate(), requestDto.getTitle(), requestDto.getContent());

    // CacheManager에서 분리된 캐시 이름("calendar_monthly", "calendar_daily")을 정확히 찾아서 지움
    if (cacheManager.getCache("calendar_monthly") != null) {
      cacheManager.getCache("calendar_monthly").evict(oldMonthlyCacheKey);
      if (!oldMonthlyCacheKey.equals(newMonthlyCacheKey)) {
        cacheManager.getCache("calendar_monthly").evict(newMonthlyCacheKey);
      }
    }

    if (cacheManager.getCache("calendar_daily") != null) {
      cacheManager.getCache("calendar_daily").evict(oldDailyCacheKey);
      if (!oldDailyCacheKey.equals(newDailyCacheKey)) {
        cacheManager.getCache("calendar_daily").evict(newDailyCacheKey);
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

    // 수동 캐시 키에서 접두사 문자열 제거
    String monthlyCacheKey = orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate().getMonthValue();
    String dailyCacheKey = orgId + ":" + findSchedule.getDate().getYear() + "-" + findSchedule.getDate().getMonthValue() + "-" + findSchedule.getDate().getDayOfMonth();

    calendarRepository.delete(findSchedule);

    // 정확한 이름의 캐시를 찾아서 삭제
    if (cacheManager.getCache("calendar_monthly") != null) {
      cacheManager.getCache("calendar_monthly").evict(monthlyCacheKey);
    }
    if (cacheManager.getCache("calendar_daily") != null) {
      cacheManager.getCache("calendar_daily").evict(dailyCacheKey);
    }
  }
}
