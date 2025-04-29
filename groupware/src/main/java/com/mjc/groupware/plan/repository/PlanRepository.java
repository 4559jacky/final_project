package com.mjc.groupware.plan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>{
	
	// start <= plan.endDate && end >= plan.startDate
	List<Plan> findByEndDateGreaterThanEqualAndStartDateLessThanEqual(LocalDate start, LocalDate end);

	List<Plan> findByDelYn(String delYn);

	
}
