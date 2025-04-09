package com.mjc.groupware.plan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PlanController {
	
	@GetMapping("/calendar")
	public String calendarView() {
		return "plan/calendar";
	}
	
}
