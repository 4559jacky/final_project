package com.mjc.groupware.plan.controller;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.plan.dto.PlanDto;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.service.PlanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PlanController {
	
	private final PlanService planService;

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
	    event.put("title", plan.getPlanTitle());
	    event.put("start", plan.getStartDate());
	    event.put("end", plan.getEndDate().plusDays(1));
//	    event.put("color", getColorByType(plan_type));
	    
	    Map<String, Object> extendedProps = new HashMap<>();
	    extendedProps.put("calendar", plan.getPlanType());
	    extendedProps.put("description", "content");
	    event.put("extendedProps", extendedProps);

	    events.add(event);
		}
	    return events;
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

	
	
}
