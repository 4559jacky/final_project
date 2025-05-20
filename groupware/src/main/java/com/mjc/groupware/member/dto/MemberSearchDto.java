package com.mjc.groupware.member.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MemberSearchDto {
	
	private String search_text;
	
	// 근태 날짜 조회
	private LocalDate target_date;
	
	// 연차 입사일 조회
	private int reg_date_order;
	
}
