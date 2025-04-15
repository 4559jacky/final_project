package com.mjc.groupware.plan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

	private final PlanRepository planRepository;
//	private final MemberRepository memberRepository;

	// 데이터베이스에 저장
	public Plan savePlan(PlanDto dto) {
		return planRepository.save(dto.toEntity());
	}

	// 목록조회
	public List<Plan> selectPlanAll() {
		return planRepository.findAll();
	}

	//
	public List<Plan> selectAllPlans() {
	    return planRepository.findAll();
	}

	// 상세모달창
	public Plan selectPlanById(Long planId) {
	    if (planId == null) {
	        throw new IllegalArgumentException("planId는 null일 수 없습니다.");
	    }
	    Plan plan = planRepository.findById(planId).orElse(null);
	    return plan;
	}


}
