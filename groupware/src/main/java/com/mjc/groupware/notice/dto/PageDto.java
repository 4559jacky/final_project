package com.mjc.groupware.notice.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PageDto {
	private String keyword;
	private String sort = "desc";
	private int page = 1;
	private int size = 10;
	
	public Pageable toPageable() {
		Sort.Direction direction = sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
		return PageRequest.of(this.page -1, this.size, Sort.by(direction, "regDate"));
	}
}
