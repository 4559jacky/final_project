package com.mjc.groupware.vote.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VoteResultChartDto {
	
	private String optionText; // 옵션 텍스트
	private int voteCount; // 득표 수
	private String anonymous; // 익명 여부
	private List<String> voters; // [부서]성명 리스트

}
