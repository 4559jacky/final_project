package com.mjc.groupware.plan.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	// JSON리턴(FullCalendar AJAX용)
	@GetMapping("/calendar/events")
	@ResponseBody
	public List<Map<String, Object>> getCalendarEvents(
	        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	        @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

	    List<Plan> plans = planService.findPlansBetween(start, end);

	    return plans.stream()
	            .map(plan -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("id", plan.getPlanNo());
	                map.put("title", plan.getPlanTitle());
	                map.put("start", plan.getStartDate());
	                map.put("end", plan.getEndDate());
	                map.put("plan_type", plan.getPlanType());
	                return map;
	            })
	            .collect(Collectors.toList());
	}


	//일정 등록
	@PostMapping("/plan/create")
	@ResponseBody
	public Map<String,String> createPlanApi(@RequestBody PlanDto dto){
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
