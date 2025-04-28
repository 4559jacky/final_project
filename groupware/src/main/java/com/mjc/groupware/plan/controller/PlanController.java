package com.mjc.groupware.plan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.member.service.MemberService;
import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.service.PlanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PlanController {
	
	private final PlanService planService;
	private final MemberService memberService;

	// HTML리턴(페이지 이동용)
	@GetMapping("/calendar")
	public String calendarView(Model model, @AuthenticationPrincipal MemberDetails memberDetails) {
		Member member = memberDetails.getMember();
		Long memberId = member.getMemberNo();
		
		List<Plan> planList = planService.selectPlanAllNotDeleted();
		List<Plan> resultList = new ArrayList<Plan>();
		for(Plan p : planList) {
			if("N".equals(p.getDelYn())) {
				resultList.add(p);
			}
		}
		model.addAttribute("resultList",resultList);
		model.addAttribute("memberId", memberId);
		return "plan/calendar";
	}
	
	// 달력에 db일정 뿌려주는 코드
	@GetMapping("/calendar/events")
	@ResponseBody
	public List<Map<String, Object>> getCalendarEvents(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end,@AuthenticationPrincipal MemberDetails memberDetails) {
		Member member = memberDetails.getMember();
		Long memberId = member.getMemberNo();
		
		List<Plan> plans = planService.selectAllPlans();
		List<Map<String, Object>> events = new ArrayList<>();
		
		for(Plan plan : plans) {
			String planType = plan.getPlanType();
	        Long regMemberNo = plan.getMember().getMemberNo();
	        // 방어적 null 체크 및 trim 처리
	        if ("개인".equals(planType != null ? planType.trim() : "") && !regMemberNo.equals(memberId)) {
	            continue;
	        }
			
	    Map<String, Object> event = new HashMap<>();
	    event.put("id", plan.getPlanNo());
	    event.put("title", plan.getPlanTitle());
	    event.put("start", plan.getStartDate());
	    event.put("end", plan.getEndDate());
	    event.put("color", getColorByType(plan.getPlanType()));
	    
	    Map<String, Object> extendedProps = new HashMap<>();
	    extendedProps.put("planType", plan.getPlanType());
	    extendedProps.put("description", plan.getPlanContent());
	    // 일정바앞에 부서명 뿌려주기
	    Member planMember = plan.getMember();
	    String deptName = "";
	    if(planMember != null && planMember.getDept() != null) {
	    	deptName = planMember.getDept().getDeptName();
	    }
	    extendedProps.put("deptName", deptName);
	    // 같은부서의 일정만 수정가능하게
	    Long deptNo = null;
	    if(planMember != null) {
	    	Dept dept = planMember.getDept();
	    	if(dept != null) {
	    		deptName = dept.getDeptName();
	    		deptNo = dept.getDeptNo();
	    	}
	    }
	    extendedProps.put("deptNo", deptNo);  // 부서 번호
	    
	    event.put("extendedProps", extendedProps);
	    events.add(event);
		}
	    return events;
	}

	private String getColorByType(String planType) {
	    switch (planType) {
	        case "회사": return "rgba(92, 100, 242, 1.0)";
	        case "부서": return "rgba(242, 75, 120, 1.0)";
	        case "개인": return "rgba(63, 191, 155, 1.0)";
	        case "휴가": return "rgba(242, 146, 29, 1.0)";
	        default: return "rgba(108, 117, 125, 0.5)"; // 기본 회색 투명
	    }
	}

	//일정 등록
	@PostMapping("/plan/create")
	@ResponseBody
	public Map<String,String> createPlanApi(PlanDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "일정 등록중 오류가 발생했습니다.");
		
		Plan result = planService.savePlan(dto);
		if(result != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "일정이 등록되었습니다.");
		}
		return resultMap;
	}

	// 상세모달창
	@GetMapping("/plan/detail/{id}")
	@ResponseBody
	public PlanDto getPlanDetail(@PathVariable("id") Long planId) {
	    Plan plan = planService.selectPlanById(planId);
	    PlanDto dto = new PlanDto().toDto(plan);
	    return dto;
	}

	// 상세모달창 수정
	@PostMapping("/plan/{id}/update")
	@ResponseBody
	public Map<String,String> updateTodoApi(@PathVariable("id") Long id, @RequestBody PlanDto dto,@AuthenticationPrincipal MemberDetails memberDetails){
		Member loginMember = memberDetails.getMember();
//		Long memberId = member.getMemberNo();
//		Long deptNo = member.getDept().getDeptNo();
		Map<String, String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "403"); // 기본 권한 없음
	    resultMap.put("res_msg", "수정 권한이 없습니다.");
	    System.out.println("확인 : "+dto.getLast_update_member());
	    
		 Plan plan = planService.findPlanById(id);
		    if (plan == null) {
		        resultMap.put("res_msg", "해당 일정이 존재하지 않습니다.");
		        return resultMap;
		    }

		    boolean canEdit = planService.canEditPlan(plan, loginMember);
		    if (!canEdit) return resultMap;

	    //수정처리
	    Plan result = planService.updatePlanOne(id, dto);
	    if (result != null) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "일정이 정상적으로 수정되었습니다.");
	    } else {
	        resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "일정 수정 중 오류가 발생했습니다.");
	    }
	    return resultMap;
	}

	// 상세모달창 삭제
	@PostMapping("plan/{id}")
	@ResponseBody
	public Map<String,String> deletePlanApi(@PathVariable("id") Long id,@AuthenticationPrincipal MemberDetails memberDetails){
		Map<String,String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "일정 삭제중 오류가 발생했습니다.");
	    
	    Member member = memberDetails.getMember();
		Long memberId = member.getMemberNo();
	    
	    int result = planService.deletePlan(id,memberId);
	    if(result > 0) {
	    	 resultMap.put("res_code", "200");
		     resultMap.put("res_msg", "일정이 정상적으로 삭제되었습니다.");
	    }
	    return resultMap;
	}

	
}
