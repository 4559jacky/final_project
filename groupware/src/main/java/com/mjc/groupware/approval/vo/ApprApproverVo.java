package com.mjc.groupware.approval.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.approval.dto.ApprApproverDto;
import com.mjc.groupware.approval.dto.ApprovalDto;
import com.mjc.groupware.approval.entity.ApprApprover;
import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.member.entity.Member;

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
public class ApprApproverVo {
	private Long appr_approver_no;
	private int approver_order;
	private String approver_decision_status;
	private LocalDateTime approver_decision_status_time;
	private String decision_reason;
	private Long appr_no;
	private Long approver_no;
	
	public ApprApproverVo toVo(ApprApprover apprApprover) {
		return ApprApproverVo.builder()
				.appr_approver_no(apprApprover.getApprApproverNo())
				.approver_order(apprApprover.getApproverOrder())
				.approver_decision_status(apprApprover.getApproverDecisionStatus())
				.approver_decision_status_time(apprApprover.getApproverDecisionStatusTime())
				.decision_reason(apprApprover.getDecisionReason())
				.appr_no(apprApprover.getApproval().getApprNo())
				.approver_no(apprApprover.getMember().getMemberNo())
				.build();
	}
	
	public ApprApprover toEntity() {
		return ApprApprover.builder()
				.apprApproverNo(appr_approver_no)
	            .approverOrder(approver_order) // 1부터 시작하는 결재 순서
	            .approverDecisionStatus(approver_decision_status)
	            .approverDecisionStatusTime(approver_decision_status_time)
	            .decisionReason(approver_decision_status)
	            .approval(Approval.builder().apprNo(appr_no).build())
	            .member(Member.builder().memberNo(approver_no).build())
	            .build();
	}
	
}
