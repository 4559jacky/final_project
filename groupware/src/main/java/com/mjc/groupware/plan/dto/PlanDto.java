package com.mjc.groupware.plan.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonProperty("plan_id")
	private Long plan_no;
	private String plan_title;
    private String plan_content;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    private LocalDate start_date;
    private LocalDate end_date;
    private Long reg_member_no;
    private String plan_type;
    private String color;
    
    // DB저장
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
				.color(color)
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
				.color(plan.getColor())
				.build();
	}
	
	// 조회
	public static PlanDto fromEntity(Plan plan) {
		return PlanDto.builder()
				.plan_no(plan.getPlanNo())
				.plan_title(plan.getPlanTitle())
				.start_date(plan.getStartDate())
				.end_date(plan.getEndDate())
				.plan_type(plan.getPlanType())
				.color(plan.getColor())
				.build();
	}
	
	// FullCalendar용 JSON변환
	public Map<String, Object> toFullCalendarEvent() {
		Map<String, Object> event = new HashMap<>();
		event.put("id", plan_no);
		event.put("title", plan_title);
		event.put("start", start_date.toString());
		event.put("end", end_date.plusDays(1).toString());
		event.put("color", getColorByType(plan_type));

		Map<String, Object> extendedProps = new HashMap<>();
		extendedProps.put("planType", plan_type);
		extendedProps.put("planContent", plan_content);
		event.put("extendedProps", extendedProps);

		return event;
	}

	private String getColorByType(String type) {
		switch (type) {
			case "회사": return "#007bff";
			case "부서": return "#28a745";
			case "개인": return "#ffc107";
			case "휴가": return "#dc3545";
			default: return "#6c757d"; // 기본 회색
		}
	}


	
}
