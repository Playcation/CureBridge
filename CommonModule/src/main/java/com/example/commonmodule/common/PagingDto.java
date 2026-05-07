package com.example.commonmodule.common;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagingDto<T> {

	private List<T> list;

	private Long count;
}
