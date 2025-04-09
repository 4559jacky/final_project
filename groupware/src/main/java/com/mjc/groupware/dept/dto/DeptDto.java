package com.mjc.groupware.dept.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;

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
public class DeptDto {
	
	private Long dept_no;
	private String dept_name;
	private Long member_no;
	private Long parent_dept_no;
	private String dept_phone;
	private String dept_location;
	private String dept_status = "ACTIVE";
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	public Dept toEntity(Member member, Dept parentDept) {
	    return Dept.builder()
	        .deptName(this.dept_name)
	        .deptPhone(this.dept_phone)
	        .deptLocation(this.dept_location)
	        .deptStatus(this.dept_status)
	        .member(member)
	        .parentDept(parentDept)
	        .build();
	}
	
}
