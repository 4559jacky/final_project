package com.mjc.groupware.plan.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
//	@Transactional
//	public Plan updatePlanOne(Long id, String title, String content, String startDate, String endDate) {
//		Plan plan = planRepository.findById(id).orElse(null);
//		if(plan != null) {
//			plan.setPlanTitle(title);
//			plan.setPlanContent(content);
//	        plan.setModDate(LocalDateTime.now());
//	        
//	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
//	        LocalDateTime startDateStr = LocalDateTime.parse(startDate, formatter);
//	        LocalDateTime endDateStr = LocalDateTime.parse(endDate, formatter);
//	        
//	        plan.setStartDate(startDateStr);
//	        plan.setEndDate(endDateStr);
//	        
//		}
//	    return plan;
//	    
//	}

	public Plan updatePlanOne(Long id, PlanDto dto) {
	    Plan plan = planRepository.findById(id).orElse(null);
	    if (plan == null) return null;

	    plan.setPlanTitle(dto.getPlan_title());
	    plan.setPlanContent(dto.getPlan_content());
	    plan.setPlanType(dto.getPlan_type());
	    plan.setModDate(LocalDateTime.now()); // 수정일은 현재 시간으로 설정

	    // ✅ 파싱 없이 바로 세팅
	    if (dto.getStart_date() != null) {
	        plan.setStartDate(dto.getStart_date());
	    }

	    if (dto.getEnd_date() != null) {
	        plan.setEndDate(dto.getEnd_date());
	    }

	    return planRepository.save(plan);
	}





}
