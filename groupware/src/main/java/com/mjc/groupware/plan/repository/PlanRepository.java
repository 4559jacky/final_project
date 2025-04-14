package com.mjc.groupware.plan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>{
	
	List<Plan> findByStartDateBetween(LocalDate start,LocalDate end);

}
