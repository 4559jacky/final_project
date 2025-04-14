package com.mjc.groupware.plan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
		return planRepository.save(dto.toEntity());
	}

	// 목록조회
	public List<Plan> selectPlanAll() {
		return planRepository.findAll();
	}

	//
//	public List<PlanDto> getPlansBetween(LocalDate start, LocalDate end) {
//        List<Plan> plans = planRepository.findByEndDateGreaterThanEqualAndStartDateLessThanEqual(start, end);
//        return plans.stream()
//                .map(plan -> PlanDto.fromEntity(plan)) // 또는 toDto(plan)도 OK
//                .collect(Collectors.toList());
//    }

	public List<Plan> selectAllPlans() {
	    return planRepository.findAll();
	}

	

}
