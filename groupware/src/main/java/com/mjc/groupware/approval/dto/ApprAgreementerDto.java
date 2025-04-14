package com.mjc.groupware.approval.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class ApprAgreementerDto {
	
	private Long appr_agreementer_no;
	private String agreementer_agree_status;
	private LocalDateTime agreementer_agree_status_time;
	private String agree_reason;
	private Long appr_no;
	private List<Long> agreementer_no;
	
	public List<ApprAgreementer> toEntityList() {
		List<ApprAgreementer> entityList = new ArrayList<>();
		
		for(Long no : agreementer_no) {
			ApprAgreementer agreementer = ApprAgreementer.builder()
					.agreementerAgreeStatus(agreementer_agree_status)
					.agreementerAgreeStatusTime(agreementer_agree_status_time)
					.agreeReason(agree_reason)
					.approval(Approval.builder().apprNo(appr_no).build())
					.member(Member.builder().memberNo(no).build())
					.build();
			
			entityList.add(agreementer);
		}
		
		return entityList;
	}
}
