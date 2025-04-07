package com.mjc.groupware.approval.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminApprovalController {
	
	@GetMapping("/approval")
	public String approvalView() {
		return "/approval/admin/approvalManagement";
	}
	
	@GetMapping("/approval/create")
	public String createApprovalView() {
		return "/approval/admin/createApproval";
	}
	
	
	
}
