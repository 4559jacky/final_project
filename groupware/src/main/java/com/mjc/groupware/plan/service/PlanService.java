package com.mjc.groupware.plan.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

	private final PlanRepository planRepository;

	// 데이터베이스에 저장
	public Plan savePlan(PlanDto dto) {
	   Plan entity = dto.toEntity();
	   Plan result = planRepository.save(entity);
	   return result;
    }

	// 목록조회
	public List<Plan> selectPlanAll() {
		return planRepository.findAll();
	}
	
	//
	public List<Plan> findPlansBetween(LocalDate start, LocalDate end) {
		return planRepository.findByStartDateBetween(start,end);
	}



}
