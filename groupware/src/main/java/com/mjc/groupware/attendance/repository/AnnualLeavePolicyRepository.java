package com.mjc.groupware.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;

public interface AnnualLeavePolicyRepository extends JpaRepository<AnnualLeavePolicy, Long> {
	AnnualLeavePolicy findByYear(int year);
}
