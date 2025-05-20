package com.mjc.groupware.plan.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.entity.PlanLog;

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
public class PlanLogDto {
	
	@JsonProperty("log_no")
	private Long log_no;
    private Long plan_no2;
    private Long member_no2;
    private String action_type;
    private LocalDateTime action_date;
    
 // DB저장
 	public PlanLog toEntity() {
 		return PlanLog.builder()
 				.logNo(log_no)
 				.plan(Plan.builder().planNo(plan_no2).build())
 				.member(Member.builder().memberNo(member_no2).build())
 				.actionType(action_type)
 				.actionDate(action_date)
 				.build();
 	}
 	
 	public PlanLogDto toDto(PlanLog planLog) {
 		return PlanLogDto.builder()
 				.log_no(planLog.getLogNo())
 				.plan_no2(planLog.getPlan() != null ? planLog.getPlan().getPlanNo() : null)
                .member_no2(planLog.getMember() != null ? planLog.getMember().getMemberNo() : null)
 				.action_type(planLog.getActionType())
 				.action_date(planLog.getActionDate())
 				.build();
 	}
}
