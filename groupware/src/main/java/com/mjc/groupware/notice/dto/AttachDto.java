package com.mjc.groupware.notice.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;

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
	private Long notice_no;
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
				.notice(Notice.builder().noticeNo(notice_no).build())
				.build();
	}
}
