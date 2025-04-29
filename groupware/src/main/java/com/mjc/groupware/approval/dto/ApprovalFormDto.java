package com.mjc.groupware.approval.dto;

import com.mjc.groupware.approval.entity.ApprovalForm;

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
public class ApprovalFormDto {
	
	private Long approval_form_no;
	private String approval_form_name;
	private String approval_form;
	private String approval_form_status;
	private int approval_form_type;
	
	// 1. ApprovalFormDto -> ApprovalForm(Entity)
		public ApprovalForm toEntity() {
			return ApprovalForm.builder()
					.approvalFormNo(approval_form_no)
					.approvalFormName(approval_form_name)
					.approvalForm(approval_form)
					.approvalFormStatus(approval_form_status)
					.approvalFormType(approval_form_type)
					.build();
		}
		
		
		// 2. ApprovalForm(Entity) -> ApprovalFormDto
		public ApprovalFormDto toDto(ApprovalForm approvalForm) {
			return ApprovalFormDto.builder()
					.approval_form_no(approvalForm.getApprovalFormNo())
					.approval_form_name(approvalForm.getApprovalFormName())
					.approval_form(approvalForm.getApprovalForm())
					.approval_form_status(approvalForm.getApprovalFormStatus())
					.approval_form_type(approvalForm.getApprovalFormType())
					.build();
		}

}
