package com.mjc.groupware.plan.dto;

import com.mjc.groupware.plan.entity.Plan;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class PlanDto {

	private Long plan_no;
	private String plan_name;
    private String plan_content;
    private String start_date;
    private String end_date;
    private String color;
    private int reg_member_no;
    private String repeat_yn;
    private String delete_yn;
    private String allday_yn;
    private String plan_type;
	
	public Plan toEntity() {
		return Plan.builder()
				.planNo(plan_no)
				.planName(plan_name)
				.planContent(plan_content)
				.startDate(start_date)
				.endDate(end_date)
				.color(color)
				.regMemberNo(reg_member_no)
				.repeatYn(repeat_yn)
				.deleteYn(delete_yn)
				.alldayYn(allday_yn)
				.planType(plan_type)
				.build();
	}
	
	public PlanDto toDto(Plan plan) {
		return PlanDto.builder()
				.plan_no(plan.getPlanNo())
				.plan_name(plan.getPlanName())
				.plan_content(plan.getPlanContent())
				.start_date(plan.getStartDate())
				.end_date(plan.getEndDate())
				.color(plan.getColor())
				.reg_member_no(plan.getRegMemberNo())
				.repeat_yn(plan.getRepeatYn())
				.delete_yn(plan.getDeleteYn())
				.allday_yn(plan.getAlldayYn())
				.plan_type(plan.getPlanType())
				.build();
	}
	
}
