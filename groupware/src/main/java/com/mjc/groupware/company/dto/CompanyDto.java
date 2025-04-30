package com.mjc.groupware.company.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

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
	private String theme_color;
	private String company_initial;
	private int rule_status;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private String light_logo_path;
	private String dark_logo_path;
	
	private MultipartFile profile_image;
	
	private String profile_image_path;
	
}
