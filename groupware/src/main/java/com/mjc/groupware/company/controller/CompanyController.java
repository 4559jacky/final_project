package com.mjc.groupware.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyController {
	
	@GetMapping("/admin/company/create")
	public String createCompany() {
		
		return "/company/create";
	}
	
}
