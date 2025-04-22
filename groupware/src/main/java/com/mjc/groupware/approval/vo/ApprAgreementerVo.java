package com.mjc.groupware.approval.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.approval.dto.ApprAgreementerDto;
import com.mjc.groupware.approval.entity.ApprAgreementer;
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
public class ApprAgreementerVo {
	
	private Long appr_agreementer_no;
	private String agreementer_agree_status;
	private LocalDateTime agreementer_agree_status_time;
	private String agree_reason;
	private Long appr_no;
	private Long agreementer_no;
	
	public ApprAgreementer toEntity() {
			return ApprAgreementer.builder()
					.agreementerAgreeStatus(agreementer_agree_status)
					.agreementerAgreeStatusTime(agreementer_agree_status_time)
					.agreeReason(agree_reason)
					.approval(Approval.builder().apprNo(appr_no).build())
					.member(Member.builder().memberNo(agreementer_no).build())
					.build();
	}
	
	public ApprAgreementerVo toVo(ApprAgreementer apprAgreementer) {
		return ApprAgreementerVo.builder()
				.appr_agreementer_no(apprAgreementer.getApprAgreementerNo())
				.agreementer_agree_status(apprAgreementer.getAgreementerAgreeStatus())
				.agreementer_agree_status_time(apprAgreementer.getAgreementerAgreeStatusTime())
				.agree_reason(apprAgreementer.getAgreeReason())
				.appr_no(apprAgreementer.getApproval().getApprNo())
				.agreementer_no(apprAgreementer.getMember().getMemberNo())
				.build();
	}
	
}