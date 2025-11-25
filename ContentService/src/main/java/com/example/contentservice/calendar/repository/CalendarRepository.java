package com.example.contentservice.calendar.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.contentservice.calendar.entity.Schedules;

public interface CalendarRepository extends JpaRepository<Schedules, Long> {

	default Schedules findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow();
	}

	List<Schedules> findAllByOrgIdAndDateBetween(Long orgId, LocalDate start, LocalDate end);
}
