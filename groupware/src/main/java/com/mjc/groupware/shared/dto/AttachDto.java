package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.shared.entity.Attach;
import com.mjc.groupware.shared.entity.Shared;

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
@Builder
@ToString
public class AttachDto {
	private Long attach_no; 
	private Long shared_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	public Attach toEntity() {
		return Attach.builder()
				.attachNo(attach_no)
				.oriName(ori_name)
				.newName(new_name)
				.attachPath(attach_path)
				.shared(Shared.builder().sharedNo(shared_no).build())
				.build();
	}
}
