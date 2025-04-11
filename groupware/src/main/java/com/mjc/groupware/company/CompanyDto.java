package com.mjc.groupware.company;

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
public class CompanyDto {
	
	private Long company_no;
	private String company_name;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
}
