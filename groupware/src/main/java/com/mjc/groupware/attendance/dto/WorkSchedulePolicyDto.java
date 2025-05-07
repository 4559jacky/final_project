package com.mjc.groupware.attendance.dto;

import java.time.LocalTime;

import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;

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
public class WorkSchedulePolicyDto {
	private Long work_schedule_policy_no;
	private LocalTime start_time_min;
	private LocalTime start_time_max;
	private double work_duration;
	private double week_work_min_time;
	private double week_work_max_time;
	private String apply_to_all;
	
	public WorkSchedulePolicy toEntity () {
		return WorkSchedulePolicy.builder()
				.workSchedulePolicyNo(work_schedule_policy_no)
				.startTimeMin(start_time_min)
				.startTimeMax(start_time_max)
				.workDuration(work_duration)
				.weekWorkMinTime(week_work_min_time)
				.weekWorkMaxTime(week_work_max_time)
				.applyToAll(apply_to_all)
				.build();
	}
	
	public WorkSchedulePolicyDto toDto(WorkSchedulePolicy entity) {
		return WorkSchedulePolicyDto.builder()
				.work_schedule_policy_no(entity.getWorkSchedulePolicyNo())
				.start_time_min(entity.getStartTimeMin())
				.start_time_max(entity.getStartTimeMax())
				.work_duration(entity.getWorkDuration())
				.week_work_min_time(entity.getWeekWorkMinTime())
				.week_work_max_time(entity.getWeekWorkMaxTime())
				.apply_to_all(entity.getApplyToAll())
				.build();
	}
}
