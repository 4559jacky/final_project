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
	public String calendarView(Model model) {
		List<Plan> resultList = planService.selectPlanAll();
		model.addAttribute("resultList",resultList);
		return "plan/calendar";
	}
	
	// 달력에 db일정 뿌려주는 코드
	@GetMapping("/calendar/events")
	@ResponseBody
	public List<Map<String, Object>> getCalendarEvents(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end,@AuthenticationPrincipal MemberDetails memberDetails) {

		Member member = memberDetails.getMember();
		Long memberId = member.getMemberNo();
		System.out.println("로그인한 memberId 확인 :"+memberId);
		
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
	    System.out.println("부서 데이터 확인:"+deptName);
	    
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
		System.out.println(dto);
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
	public Map<String,String> updateTodoApi(@PathVariable("id") Long id, @RequestBody PlanDto dto){
		System.out.println("id값:"+id);
		System.out.println("받은 title: " + dto.getPlan_title());
		System.out.println("받은 start_date: " + dto.getStart_date());

	    Map<String,String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "일정 수정 중 오류가 발생했습니다.");

	    Plan result = planService.updatePlanOne(id, dto);
	    if(result != null) {
	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "일정이 정상적으로 수정되었습니다.");
	    }
	    return resultMap;
	}

	// 상세모달창 삭제
	@DeleteMapping("plan/{id}")
	@ResponseBody
	public Map<String,String> deletePlanApi(@PathVariable("id") Long id){
		Map<String,String> resultMap = new HashMap<>();
	    resultMap.put("res_code", "500");
	    resultMap.put("res_msg", "일정 삭제중 오류가 발생했습니다.");
	    
	    int result = planService.deletePlan(id);
	    if(result > 0) {
	    	 resultMap.put("res_code", "200");
		     resultMap.put("res_msg", "일정이 정상적으로 삭제되었습니다.");
	    }
	    
	    return resultMap;
	}

	
}
