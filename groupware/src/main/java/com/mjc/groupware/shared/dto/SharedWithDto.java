package com.mjc.groupware.shared.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.shared.entity.SharedWith;

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
public class SharedWithDto {
	private Long with_no;
	private Long file_no;
	private Long with_member_no;
	private int with_permission;
	private int with_type;
	private LocalDateTime reg_date;
	
	public SharedWith toEntity() {
		return SharedWith.builder()
				.withNo(with_no)
				.withPermission(with_permission)
				.withType(with_type)
				.build();
				
	}
	
	public SharedWithDto toDto(SharedWith with) {
		return SharedWithDto.builder()
				.with_no(with.getWithNo())
				.with_permission(with.getWithPermission())
				.with_type(with.getWithType())
				.build();
	}
}
