package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
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
@ToString
@Builder
public class SharedDto {
	private Long shared_no;
	private String shared_title;
	private String shared_content;
	private char shared_status;
	private int views;
	private Long member_no;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private List<MultipartFile> files;
	
	public Shared toEntity() {
		return Shared.builder()
				.sharedNo(shared_no)
				.sharedTitle(shared_title)
				.sharedContent(shared_content)
				.views(views)
				.build();
	}
	
	public SharedDto toDto(Shared shared) {
		return SharedDto.builder()
				.shared_no(shared.getSharedNo())
				.shared_title(shared.getSharedTitle())
				.shared_content(shared.getSharedContent())
				.views(shared.getViews())
				.build();
	}
}
