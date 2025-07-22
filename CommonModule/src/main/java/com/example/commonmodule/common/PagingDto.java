package com.example.commonmodule.common;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PagingDto<T> {

	private final List<T> list;

	private final Long count;
}
