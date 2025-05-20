package com.mjc.groupware.attendance.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "work_schedule_policy")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSchedulePolicy {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="work_schedule_policy_no")
	private Long workSchedulePolicyNo;
	
	@Column(name="start_time_min")
	private LocalTime startTimeMin;
	
	@Column(name="start_time_max")
	private LocalTime startTimeMax;
	
	@Column(name="work_duration")
	private double workDuration;
	
	@Column(name="week_work_min_time")
	private double weekWorkMinTime;
	
	@Column(name="week_work_max_time")
	private double weekWorkMaxTime;
	
	@Column(name="apply_to_all")
	private String applyToAll;
	
}
