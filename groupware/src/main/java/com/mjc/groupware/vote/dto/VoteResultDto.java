package com.mjc.groupware.vote.dto;

import java.time.LocalDateTime;

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
public class VoteResultDto {
	
	private Long result_no; // 투표 결과
	private Long vote_no; // 투표 번호
	private Long option_no; // 선택한 옵션
	private Long member_no; // 투표한 사용자(익명일 경우 NULL)
	private LocalDateTime vote_time; // 투표 시간
}
