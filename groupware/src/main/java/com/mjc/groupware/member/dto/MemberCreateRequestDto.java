package com.mjc.groupware.member.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class MemberCreateRequestDto {
	// 사원 등록 :: post - 화면단 -> 컨트롤러 :: 객체지향을 위해 따로 떼어내서 사용
	
	private Long member_no;
	private String member_id;
	private String member_pw;
	private String member_new_pw;
	private String member_name;
	private String member_birth;
	private String member_gender;
	private String member_addr1;
	private String member_addr2;
	private String member_addr3;
	private String member_email;
	private String member_phone;
	private Long pos_no;
	private Long dept_no;
	private Long role_no = (long) 2;
	private int status = 100;
	private String dept_name;
	private String pos_name;
	private String role_name;
	private LocalDate hire_date;
	private LocalDate reg_date;
	private LocalDateTime mod_date;
	private LocalDateTime end_date;
	private double annual_leave;
	private String signature;

}
