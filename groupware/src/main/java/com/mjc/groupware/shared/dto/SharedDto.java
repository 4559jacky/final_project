package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

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
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	public Shared toEntity() {
		return Shared.builder()
				.sharedNo(shared_no)
				.sharedTitle(shared_content)
				.sharedContent(shared_content)
				.build();
	}
	
	public SharedDto toDto(Shared shared) {
		return SharedDto.builder()
				.shared_no(shared.getSharedNo())
				.shared_title(shared.getSharedTitle())
				.shared_content(shared.getSharedContent())
				.build();
	}
}
