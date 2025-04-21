package com.mjc.groupware.company.dto;

import java.util.List;

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
public class FuncDetailResponseDto {
	
	private String res_code;
	private String res_msg;
	
	private FuncDto funcDto;
	private List<FuncDto> funcDtoList;
}
