package com.mjc.groupware.attendance.dto;

import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;

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
public class AnnualLeavePolicyDto {
	private Long annual_leave_policy_no;
	private int year;
	private double leave_days;
	
	public AnnualLeavePolicy toEntity () {
		return AnnualLeavePolicy.builder()
				.annualLeavePolicyNo(annual_leave_policy_no)
				.year(year)
				.leaveDays(leave_days)
				.build();
	}
	
	public AnnualLeavePolicyDto toDto(AnnualLeavePolicy entity) {
		return AnnualLeavePolicyDto.builder()
				.annual_leave_policy_no(entity.getAnnualLeavePolicyNo())
				.year(entity.getYear())
				.leave_days(entity.getLeaveDays())
				.build();
	}
}
