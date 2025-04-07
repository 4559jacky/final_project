package com.mjc.groupware.pos.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.pos.entity.Pos;

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
public class PosDto {
	
	private Long pos_no;
	private String pos_name;
	private Long pos_order;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	public Pos toEntity() {
		return Pos.builder()
				.posNo(this.getPos_no())
				.posName(this.getPos_name())
				.posOrder(this.getPos_order())
				.build();
	}
	
	public PosDto toDto(Pos pos) {
		return PosDto.builder()
				.pos_no(pos.getPosNo())
				.pos_name(pos.getPosName())
				.pos_order(pos.getPosOrder())
				.build();
	}
	
}
