package com.mjc.groupware.plan.service;

import java.time.LocalDateTime;
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

	// 상세모달창 수정
	public Plan updatePlanOne(Long id, PlanDto dto) {
	    Plan plan = planRepository.findById(id).orElse(null);
	    if (plan == null) return null;

	    plan.setPlanTitle(dto.getPlan_title());
	    plan.setPlanContent(dto.getPlan_content());
	    plan.setPlanType(dto.getPlan_type());
	    plan.setModDate(LocalDateTime.now()); // 수정일은 현재 시간으로 설정

	    // 파싱 없이 바로 세팅
	    if (dto.getStart_date() != null) {
	        plan.setStartDate(dto.getStart_date());
	    }

	    if (dto.getEnd_date() != null) {
	        plan.setEndDate(dto.getEnd_date());
	    }

	    return planRepository.save(plan);
	}

	// 상세모달창 삭제
	public int deletePlan(Long id) {
		int result = 0;
		try {
			Plan target = planRepository.findById(id).orElse(null);
			if(target != null) {
				planRepository.deleteById(id);
			}
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}


}
