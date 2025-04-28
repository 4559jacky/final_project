package com.mjc.groupware.plan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.member.entity.Member;
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
//	public List<Plan> selectPlanAll() {
//		return planRepository.findAll();
//	}
//
//	//
	public List<Plan> selectAllPlans() {
	    return planRepository.findAll();
	}

	public List<Plan> selectPlanAllNotDeleted() {
	    return planRepository.findByDelYn("N");
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
	    plan.setLastUpdateMember(dto.getLast_update_member());
	    plan.setDelYn(dto.getDel_yn());

	    // 파싱 없이 바로 세팅
	    if (dto.getStart_date() != null) {
	        plan.setStartDate(dto.getStart_date());
	    }

	    if (dto.getEnd_date() != null) {
	        plan.setEndDate(dto.getEnd_date());
	    }

	    return planRepository.save(plan);
	}

	// 상세모달 수정 권한체크
	public Plan findPlanById(Long id) {
	    return planRepository.findById(id).orElse(null);
	}
	public boolean canEditPlan(Plan plan, Member loginMember) {
	    Member writer = plan.getMember(); // plan에는 member가 join돼 있다고 가정
	    return writer.getMemberNo().equals(loginMember.getMemberNo()) ||
	           writer.getDept().getDeptNo().equals(loginMember.getDept().getDeptNo());
	}

	// 상세모달창 삭제
//	public int deletePlan(Long id) {
//		int result = 0;
//		try {
//			Plan target = planRepository.findById(id).orElse(null);
//			if(target != null) {
//				planRepository.deleteById(id);
//			}
//			result = 1;
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
	@Transactional
	public int deletePlan(Long id, Long memberId) {
	    int result = 0;
	    try {
	    	Plan target = planRepository.findById(id).orElse(null);
	    	target.setLastUpdateMember(memberId);
	    	planRepository.save(target);
	    	
	    	target = planRepository.findById(id).orElse(null);
	    	
	        if (target != null) {
	            target.setDelYn("Y"); // del_yn 값을 'Y'로 변경
	            planRepository.save(target); // 수정된 내용 저장
	            result = 1;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	


}
