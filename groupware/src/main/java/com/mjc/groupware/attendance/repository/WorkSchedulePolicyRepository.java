package com.mjc.groupware.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;

public interface WorkSchedulePolicyRepository extends JpaRepository<WorkSchedulePolicy, Long> {

}
