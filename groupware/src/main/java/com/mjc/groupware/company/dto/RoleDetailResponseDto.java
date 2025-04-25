package com.mjc.groupware.company.dto;

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
public class RoleDetailResponseDto {
	
	private String res_code;
	private String res_msg;
	
	private Long func_no;
    private String func_name;
    
}
