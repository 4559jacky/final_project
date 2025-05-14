package com.mjc.groupware.plan.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.dto.PlanLogDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.entity.PlanLog;
import com.mjc.groupware.plan.repository.PlanLogRepository;
import com.mjc.groupware.plan.repository.PlanRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

	private final PlanRepository planRepository;
	private final PlanLogRepository planLogRepository;
//	private final MemberRepository memberRepository;

	// 일정 데이터베이스에 저장
	public Plan savePlan(PlanDto dto) {
		return planRepository.save(dto.toEntity());
	}

	// 일정 전체 조회
	public List<Plan> selectPlanAll() {
		return planRepository.findAll();
	}

	public List<Plan> selectAllPlans() {
	    return planRepository.findAll();
	}

	// 삭제 Y/N 확인
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
	@Transactional
	public Plan updatePlanOne(Long id, PlanDto dto) {
	    Plan plan = planRepository.findById(id).orElse(null);
	    if (plan == null) return null;

	    plan.setPlanTitle(dto.getPlan_title());
	    plan.setPlanContent(dto.getPlan_content());
	    plan.setPlanType(dto.getPlan_type());
	    plan.setModDate(LocalDateTime.now()); // 수정일은 현재 시간으로 설정
	    plan.setLastUpdateMember(dto.getLast_update_member());
	    plan.setDelYn("N");

	    // 파싱 없이 바로 세팅
	    if (dto.getStart_date() != null) {
	        plan.setStartDate(dto.getStart_date());
	    }

	    if (dto.getEnd_date() != null) {
	        plan.setEndDate(dto.getEnd_date());
	    }

	    Plan updated = planRepository.save(plan);
	    savePlanLog(id,dto.getLast_update_member(),"수정");
	    
	    return updated;
	}

	@Transactional
	public void savePlanLog(Long planId, Long memberNo, String actionType) {
		if (planId == null || memberNo == null) {
	        System.err.println("PlanLog 저장 실패: planId 또는 memberNo가 null입니다.");
	        return;
	    }
	    PlanLog log = PlanLog.builder()
	            .plan(Plan.builder().planNo(planId).build())
	            .member(Member.builder().memberNo(memberNo).build())
	            .actionType(actionType)
	            .build();

	    planLogRepository.save(log);
	}
	
	public List<PlanLogDto> getLogsByPlanId(Long planId) {
	    return planLogRepository.findByPlan_PlanNo(planId).stream()
	            .map(log -> new PlanLogDto().toDto(log))
	            .collect(Collectors.toList());
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
	@Transactional
	public int deletePlan(Long id, Long memberId) {
	    int result = 0;
	    try {
	    	Plan target = planRepository.findById(id).orElse(null);
	    	target.setLastUpdateMember(memberId);
	    	planRepository.save(target);

	        if (target != null) {
	            target.setDelYn("Y");
	            planRepository.save(target);

	            // 삭제 로그 저장
	            savePlanLog(id, memberId, "삭제");
	            result = 1;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	// 당일 근태관리를 위한 휴가 데이터 가져오기
	public Plan selectAnnualPlan(Member member, LocalDate today) {
	    LocalDateTime startOfDay = today.atStartOfDay();           // 2025-05-08T00:00
	    LocalDateTime endOfDay = today.atTime(23, 59, 59);         // 2025-05-08T23:59:59

	    return planRepository
	            .findByMemberAndStartDateBetween(member, startOfDay, endOfDay, "휴가")
	            .orElse(null);
	}
	


}
