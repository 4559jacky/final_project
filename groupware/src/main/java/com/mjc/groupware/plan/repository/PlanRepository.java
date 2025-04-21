package com.mjc.groupware.plan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>{
	
	// 날짜 범위로 일정 조회
//	List<Plan> findByStartDateBetween(LocalDate start,LocalDate end);
	
	// start <= plan.endDate && end >= plan.startDate
	List<Plan> findByEndDateGreaterThanEqualAndStartDateLessThanEqual(LocalDate start, LocalDate end);

}
