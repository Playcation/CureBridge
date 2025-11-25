package com.example.contentservice.calendar.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.contentservice.calendar.dto.CreateScheduleRequestDto;
import com.example.contentservice.calendar.dto.CreateScheduleResponseDto;
import com.example.contentservice.calendar.dto.FindScheduleRequestDto;
import com.example.contentservice.calendar.dto.ScheduleResponseDto;
import com.example.contentservice.calendar.dto.UpdateScheduleRequestDto;
import com.example.contentservice.calendar.entity.Schedules;
import com.example.contentservice.calendar.repository.CalendarRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalendarService {
	private final CalendarRepository calendarRepository;

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

	public List<ScheduleResponseDto> findAllSchedulesByMonth(Long orgId, FindScheduleRequestDto requestDto) {
		LocalDate curr = requestDto.getDate();
		LocalDate start = curr.withDayOfMonth(1);
		LocalDate end = curr.withDayOfMonth(curr.lengthOfMonth());

		List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId, start, end);

		return schedules.stream().map(ScheduleResponseDto::new).toList();
	}

	public List<ScheduleResponseDto> findAllSchedulesByDate(Long orgId, FindScheduleRequestDto requestDto) {
		List<Schedules> schedules = calendarRepository.findAllByOrgIdAndDateBetween(orgId, requestDto.getDate(),
			requestDto.getDate());

		return schedules.stream().map(ScheduleResponseDto::new).toList();
	}

	public ScheduleResponseDto updateSchedule(Long id, Long orgId, UpdateScheduleRequestDto requestDto) {
		Schedules findSchedule = calendarRepository.findByIdOrElseThrow(id);
		if (!findSchedule.getOrgId().equals(orgId)) {
			// TODO: orgID 매칭하지 않음. 예외 발생
		}
		findSchedule.update(requestDto.getDate(), requestDto.getTitle(), requestDto.getContent());
		return new ScheduleResponseDto(findSchedule);
	}

	public void deleteSchedule(Long id, Long orgId) {
		Schedules findSchedule = calendarRepository.findByIdOrElseThrow(id);
		if (!findSchedule.getOrgId().equals(orgId)) {
			// TODO: orgID 매칭하지 않음. 예외 발생
		}
		calendarRepository.delete(findSchedule);
	}
}
