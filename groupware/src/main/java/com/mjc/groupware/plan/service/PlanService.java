package com.mjc.groupware.plan.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

//	public Plan updatePlanOne(Long plan_no, String plan_title, String plan_content, LocalDateTime start_date,
//			LocalDateTime end_date, String plan_type) {
//		Plan plan = planRepository.findById(plan_no).orElse(null);
//		if(plan != null) {
//			plan.setPlanNo(plan_no);
//			plan.setPlanTitle(plan_title);
//			plan.setPlanContent(plan_content);
//			plan.setModDate(LocalDateTime.now());
//			plan.setStartDate(start_date);
//	        plan.setEndDate(end_date);
//		}
//	    return plan;
//	}

	public Plan updatePlanOne(Long id) {
		Plan target = planRepository.findById(id).orElse(null);
		PlanDto dto = PlanDto.builder()
				.plan_no(target.getPlanNo())
				.plan_title(target.getPlanTitle())
				.plan_content(target.getPlanContent())
				.start_date(target.getStartDate())
				.end_date(target.getEndDate())
				.build();

		return planRepository.save(dto.toEntity());
	}


}
