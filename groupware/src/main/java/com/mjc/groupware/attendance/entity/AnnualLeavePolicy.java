package com.mjc.groupware.attendance.entity;

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
@Table(name = "annual_leave_policy")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnnualLeavePolicy {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="annual_leave_policy_no")
	private Long annualLeavePolicyNo;
	
	@Column(name="year")
	private int year;
	
	@Column(name="leave_days")
	private double leaveDays;
	
}
