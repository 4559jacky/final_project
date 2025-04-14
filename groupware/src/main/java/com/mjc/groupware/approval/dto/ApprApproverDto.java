package com.mjc.groupware.approval.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class ApprApproverDto {
	private Long appr_approver_no;
	private int approver_order;
	private String approver_decision_status;
	private LocalDateTime approver_decision_status_time;
	private String decision_reason;
	private Long appr_no;
	
	// 여러 명의 결재자
	private List<Long> approver_no;
	
	public List<ApprApprover> toEntityList() {
	    List<ApprApprover> entityList = new ArrayList<>();

	    for (int i = 0; i < approver_no.size(); i++) {
	        Long no = approver_no.get(i);

	        ApprApprover approver = ApprApprover.builder()
	            .approverOrder(i + 1) // 1부터 시작하는 결재 순서
	            .approverDecisionStatus(approver_decision_status)
	            .approverDecisionStatusTime(approver_decision_status_time)
	            .decisionReason(approver_decision_status)
	            .approval(Approval.builder().apprNo(appr_no).build())
	            .member(Member.builder().memberNo(no).build())
	            .build();

	        entityList.add(approver);
	    }

	    return entityList;
	}
}
