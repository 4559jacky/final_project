package com.mjc.groupware.plan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public List<Map<String, Object>> getCalendarEvents(@RequestParam(name = "start") String start, @RequestParam(name = "end") String end) {
	    // 임시 하드코딩
		System.out.println("start = " + start);
		System.out.println("end = " + end);
	    
		List<Plan> plans = planService.selectAllPlans();
		
		List<Map<String, Object>> events = new ArrayList<>();

		for(Plan plan : plans) {
	    Map<String, Object> event = new HashMap<>();

	    event.put("id", plan.getPlanNo());
	    event.put("title", plan.getPlanTitle());
	    event.put("start", plan.getStartDate());
	    event.put("end", plan.getEndDate());
	    event.put("color", getColorByType(plan.getPlanType()));
	    
	    Map<String, Object> extendedProps = new HashMap<>();
	    extendedProps.put("calendar", plan.getPlanType());
	    extendedProps.put("description", plan.getPlanContent());

	    event.put("extendedProps", extendedProps);

	    events.add(event);
		}
	    return events;
	}


	private String getColorByType(String planType) {
	    switch (planType) {
	        case "회사": return "rgba(92, 100, 242, 0.7)";
	        case "부서": return "rgba(242, 75, 120, 0.7)";
	        case "개인": return "rgba(63, 191, 155, 0.7)";
	        case "휴가": return "rgba(242, 146, 29, 0.7)";
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
//		System.out.println("planId"+planId);
	    Plan plan = planService.selectPlanById(planId);
//	    System.out.println(plan);
	    PlanDto dto = new PlanDto().toDto(plan);
//	    System.out.println(dto);
	    return dto;
	}

	// 상세모달창 수정
//	@PostMapping("/plan/update/{id}")
//	@ResponseBody
//	public Map<String, String> updatePlanApi(
//	    @PathVariable Long id,
//	    @RequestParam String title,
//	    @RequestParam String content,
//	    @RequestParam LocalDateTime startDate,
//	    @RequestParam LocalDateTime endDate,
//	    @RequestParam String planType) {
//	    
//	    Map<String, String> resultMap = new HashMap<>();
//	    resultMap.put("res_code", "500");
//	    resultMap.put("res_msg", "일정 수정 중 오류가 발생했습니다.");
//
//	    try {
//	        Plan updated = planService.updatePlanOne(
//	            id,  
//	            title,  // 각 필드를 @RequestParam으로 받아서 처리
//	            content,
//	            startDate,
//	            endDate,
//	            planType
//	        );
//	        
//	        if (updated != null) {
//	            resultMap.put("res_code", "200");
//	            resultMap.put("res_msg", "일정이 정상적으로 수정되었습니다.");
//	        }
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//
//	    return resultMap;
//	}
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

	


	
	
	
	
	
	
	
	
	
	
}
