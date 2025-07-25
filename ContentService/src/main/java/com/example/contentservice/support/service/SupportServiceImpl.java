package com.example.contentservice.support.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.commonmodule.common.PagingDto;
import com.example.contentservice.support.dto.SupportRequestDto;
import com.example.contentservice.support.dto.SupportResponseDto;
import com.example.contentservice.support.entity.Support;
import com.example.contentservice.support.repository.SupportRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {
	private final SupportRepository supportRepository;

	@Transactional
	public SupportResponseDto createSupport(SupportRequestDto requestDto, Long userId) {

		Support support = Support.builder()
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.userId(userId)
			.build();
		Support savedSupport = supportRepository.save(support);
		return SupportResponseDto.toDto(savedSupport);
	}

	public SupportResponseDto getSupport(Long supportId) {
		Support support = supportRepository.findByIdOrElseThrow(supportId);
		return SupportResponseDto.toDto(support);
	}

	public PagingDto<SupportResponseDto> getSupportsAndPaging(int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));
		Page<Support> supportPage = supportRepository.findAll(pageable);

		List<SupportResponseDto> supportDtoList = supportPage.getContent().stream()
			.map(SupportResponseDto::toDto)
			.toList();

		return new PagingDto<>(supportDtoList, supportPage.getTotalElements());
	}

	@Transactional
	public void deleteSupport(Long supportId) {
		supportRepository.findByIdOrElseThrow(supportId);
		supportRepository.deleteById(supportId);
	}
}
