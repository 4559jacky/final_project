package com.mjc.groupware.approval.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalStatusTypeDto {
	private int count_A;
	private int count_D;
	private int count_R;
	private int count_C;
}
