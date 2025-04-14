package com.mjc.groupware.member.dto;

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
public class MemberAttachDto {
	
	private Long attach_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private Long member_no;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private MultipartFile profile_image;
	
}
