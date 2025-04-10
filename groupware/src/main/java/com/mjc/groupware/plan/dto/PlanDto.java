package com.mjc.groupware.plan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mjc.groupware.plan.entity.Plan;

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
public class PlanDto {

	private Long plan_no;
	private String plan_title;
    private String plan_content;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    private LocalDate start_date;
    private LocalDate end_date;
    private Long reg_member_no;
    private String plan_type;
	
	public Plan toEntity() {
		return Plan.builder()
				.planNo(plan_no)
				.planTitle(plan_title)
				.planContent(plan_content)
				.regDate(reg_date)
				.modDate(mod_date)
				.startDate(start_date)
				.endDate(end_date)
				.regMemberNo(reg_member_no)
				.planType(plan_type)
				.build();
	}
	
	public PlanDto toDto(Plan plan) {
		return PlanDto.builder()
				.plan_no(plan.getPlanNo())
				.plan_title(plan.getPlanTitle())
				.plan_content(plan.getPlanContent())
				.reg_date(plan.getRegDate())
				.mod_date(plan.getModDate())
				.start_date(plan.getStartDate())
				.end_date(plan.getEndDate())
				.reg_member_no(plan.getRegMemberNo())
				.plan_type(plan.getPlanType())
				.build();
	}
	
}
