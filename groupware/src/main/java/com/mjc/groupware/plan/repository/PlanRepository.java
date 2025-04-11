package com.mjc.groupware.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>{


}
