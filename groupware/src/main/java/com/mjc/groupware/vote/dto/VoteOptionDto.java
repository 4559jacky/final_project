package com.mjc.groupware.vote.dto;

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
public class VoteOptionDto {
	 	private Long option_no;
	    private Long vote_no;
	    private String option_text;
	    private Integer order_no;
}
