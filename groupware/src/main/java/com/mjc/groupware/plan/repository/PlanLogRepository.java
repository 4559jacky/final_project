package com.mjc.groupware.plan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.plan.entity.PlanLog;

public interface PlanLogRepository extends JpaRepository<PlanLog, Long>{

	List<PlanLog> findByPlan_PlanNo(Long planNo);

}
