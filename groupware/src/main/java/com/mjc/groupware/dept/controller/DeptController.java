package com.mjc.groupware.dept.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeptController {
	
	@GetMapping("/admin/dept/create")
	public String createDeptView() {
		return "dept/create";
	}
	
}
