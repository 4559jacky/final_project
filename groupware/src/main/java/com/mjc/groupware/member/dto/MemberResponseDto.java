package com.mjc.groupware.member.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class MemberResponseDto {
	
	private String res_code;
	private String res_msg;
	
	@JsonProperty("dept_no")
	private Long dept_no;
	
	private List<MemberDto> member_list_by_dept;
	
}
