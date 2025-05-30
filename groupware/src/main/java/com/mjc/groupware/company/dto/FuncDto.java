package com.mjc.groupware.company.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.member.dto.RoleDto;

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
public class FuncDto {
	
	private Long func_no;
	private String func_name;
	private String func_code;
	private int func_status;
	private Long parent_func_no;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private List<RoleDto> accessibleRoles;
	
}
