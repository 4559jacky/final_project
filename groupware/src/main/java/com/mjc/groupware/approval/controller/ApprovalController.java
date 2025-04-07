package com.mjc.groupware.approval.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApprovalController {
	
	// 관리자만 접근 가능한 url
	@GetMapping("/admin/approval")
	public String approvalAdminView() {
		return "/approval/admin/approvalManagement";
	}
	
	@GetMapping("/admin/approval/create")
	public String createApprovalAdminView() {
		return "/approval/admin/createApproval";
	}
	
	// 인증받은 모든 사원이 접근 가능한 url
	@GetMapping("/approval")
	public String approvalView() {
		return "/approval/user/approval";
	}
	
}
