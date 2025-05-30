package com.mjc.groupware.plan.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reg_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime mod_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start_date;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end_date;
    private Long reg_member_no;
    private String plan_type;
    private String color;
    private String member_name;
    private String dept_name;
    private String member_no;
    private Long dept_no;
    private Long last_update_member;
    private String del_yn;
    private Long appr_no;
    
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
				.member(Member.builder().memberNo(reg_member_no).build())
				.planType(plan_type)
				.color(color)
				.lastUpdateMember(last_update_member)
				.delYn(del_yn)
				.apprNo(appr_no)
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
				.reg_member_no(plan.getMember().getMemberNo())
				.plan_type(plan.getPlanType())
				.color(plan.getColor())
				.member_name(plan.getMember() != null ? plan.getMember().getMemberName() : null)
				.dept_name(plan.getMember().getDept() != null ? plan.getMember().getDept().getDeptName() : null)
				.dept_no(plan.getMember().getDept() != null ? plan.getMember().getDept().getDeptNo() : null)
				.last_update_member(plan.getLastUpdateMember())
				.del_yn(plan.getDelYn())
				.appr_no(plan.getApprNo())
				.build();
	}
	
	// FullCalendar용 JSON변환
	public Map<String, Object> toFullCalendarEvent() {
		
		Map<String, Object> event = new HashMap<>();
		event.put("id", plan_no);
		event.put("title", plan_title);
		event.put("start", start_date.toString());
		event.put("end", end_date.toString());
		event.put("color", getColorByType(plan_type));

		Map<String, Object> extendedProps = new HashMap<>();
		extendedProps.put("planType", plan_type);
		extendedProps.put("planContent", plan_content);
		extendedProps.put("memberName", member_name);
		extendedProps.put("deptName", dept_name);
		extendedProps.put("deptName", dept_no);

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
