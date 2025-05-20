package com.mjc.groupware.dept.dto;

import java.util.List;

import com.mjc.groupware.member.dto.MemberDto;

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
public class DeptResponseDto {
	
	private Long deptNo;
    private String deptName;
    private int deptStatus;
    private String deptLocation;
    private String deptPhone;
    private Long memberNo;
    private Long parentDeptNo;
    
    private List<MemberDto> members;
	
}
