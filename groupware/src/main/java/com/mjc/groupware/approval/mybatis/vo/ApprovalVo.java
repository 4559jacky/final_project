package com.mjc.groupware.approval.mybatis.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApprovalVo {
	
	private Long appr_no;
	private LocalDate appr_reg_date;
	private String appr_title;
	private String appr_status;
	private int appr_order_status;
	private String relationship;
	private String member_name;
	private String approval_form_name;
	
}
