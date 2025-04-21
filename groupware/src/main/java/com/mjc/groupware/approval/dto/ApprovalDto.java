package com.mjc.groupware.approval.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.member.entity.Member;

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
public class ApprovalDto {
	private Long appr_no;
	private LocalDateTime appr_reg_date;
	private LocalDateTime appr_res_date;
	private String appr_title;
	private String appr_text;
	private String appr_status;
	private int appr_order_status;
	private String appr_reason;
	private LocalDate start_date;
	private LocalDate end_date;
	private Long appr_sender;
	private Long approval_type_no;
	
	// 결재자, 합의자, 참조자
	private List<Long> approver_no;
	private List<Long> agreementer_no;
	private List<Long> referencer_no;
	
	public Approval toEntity() {
		return Approval.builder()
				.apprRegDate(appr_reg_date)
				.apprResDate(appr_res_date)
				.apprTitle(appr_title)
				.apprText(appr_text)
				.apprStatus(appr_status)
				.apprOrderStatus(appr_order_status)
				.apprReason(appr_reason)
				.startDate(start_date)
				.endDate(end_date)
				.member(Member.builder().memberNo(appr_sender).build())
				.approvalForm(ApprovalForm.builder().approvalFormNo(approval_type_no).build())
				.build();
	}
	
	public ApprovalDto toDto(Approval approval) {
		return ApprovalDto.builder()
				.appr_reg_date(approval.getApprRegDate())
				.appr_res_date(approval.getApprResDate())
				.appr_title(approval.getApprTitle())
				.appr_text(approval.getApprText())
				.appr_status(approval.getApprStatus())
				.appr_order_status(approval.getApprOrderStatus())
				.appr_reason(approval.getApprReason())
				.start_date(approval.getStartDate())
				.end_date(approval.getEndDate())
				.approval_type_no(approval.getApprovalForm().getApprovalFormNo())
				.build();
	}
}
