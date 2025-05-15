package com.mjc.groupware.plan.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long>{
	
	// start <= plan.endDate && end >= plan.startDate
	List<Plan> findByEndDateGreaterThanEqualAndStartDateLessThanEqual(LocalDate start, LocalDate end);

	List<Plan> findByDelYn(String delYn);
	
	
	@Query("SELECT p FROM Plan p WHERE p.member = :member AND p.startDate BETWEEN :start AND :end AND p.planType = :type")
	Optional<Plan> findByMemberAndStartDateBetween(
	    @Param("member") Member member,
	    @Param("start") LocalDateTime start,
	    @Param("end") LocalDateTime end,
	    @Param("type") String type
	);
	
	@Query("SELECT p FROM Plan p WHERE FUNCTION('DATE', p.startDate) = :today AND p.planType = :planType")
	List<Plan> findAllByStartDateAndPlanType(@Param("today") LocalDate today, @Param("planType") String planType);

	
}
