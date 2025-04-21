package com.mjc.groupware.notice.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
@ToString
@Builder
public class NoticeDto {
	private Long notice_no;
	private String notice_title;
	private String notice_content;
	private char notice_status;
	private int views;
	private Long member_no;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private List<Attach> attachList;
	private List<MultipartFile> files;
	
	public Notice toEntity() {
		return Notice.builder()
				.noticeNo(notice_no)
				.noticeTitle(notice_title)
				.noticeContent(notice_content)
				.views(views)
				.build();
	}
	
	public NoticeDto toDto(Notice notice) {
		return NoticeDto.builder()
				.notice_no(notice.getNoticeNo())
				.notice_title(notice.getNoticeTitle())
				.notice_content(notice.getNoticeContent())
				.views(notice.getViews())
				.build();
	}
}
