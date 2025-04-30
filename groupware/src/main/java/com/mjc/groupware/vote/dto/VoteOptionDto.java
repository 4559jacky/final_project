package com.mjc.groupware.vote.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class VoteOptionDto {
	
	private Long option_no; // 선택지 번호
	private Long vote_no; // 투표 번호
	private String option_text; // 선택지 내용
	private Long order_no; // 표시 순서

}
