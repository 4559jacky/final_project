package com.mjc.groupware.approval.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalAttach;

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
public class ApprovalAttachDto {
	private Long attach_no;
	private Long appr_no;
	private String ori_name;
	private String new_name;
	private String attach_path;
	private LocalDateTime reg_date;
	
	public ApprovalAttach toEntity() {
		return ApprovalAttach.builder()
				.attachNo(attach_no)
				.oriName(ori_name)
				.newName(new_name)
				.attachPath(attach_path)
				.approval(Approval.builder().apprNo(appr_no).build())
				.build();
	}
}
