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
public class VoteDto {
	private Long vote_no; // 투표 번호
	private Long board_no; // 게시판 번호
	private String vote_title; // 투표 제목
	private String is_multiple; // 복수 선택 가능 여부
	private String is_anonymous; // 익명 투표 여부
	private LocalDateTime end_date; // 투표 마감일
	private LocalDateTime reg_date; // 등록일
	
	
	}
