package com.mjc.groupware.position.dto;

import java.time.LocalDateTime;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PositionDto {
	
	private Long pos_no;
	private String pos_name;
	private Long parent_pos_no;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
}
