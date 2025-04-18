package com.mjc.groupware.member.dto;

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
public class MemberStaticInfoDto {
	
	private Long member_no;
	private String member_id;
	private String member_name;
	private String member_dept_name;
	private String member_pos_name;
	private String member_email;
	
}
