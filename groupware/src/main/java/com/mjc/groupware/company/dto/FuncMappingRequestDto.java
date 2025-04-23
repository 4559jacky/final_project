package com.mjc.groupware.company.dto;

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
public class FuncMappingRequestDto {
	
	private Long func_no;
	private Long role_no;
	
	@JsonProperty("is_checked")
	private boolean is_checked;
	
}
