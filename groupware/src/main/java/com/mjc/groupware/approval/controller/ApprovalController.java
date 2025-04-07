package com.mjc.groupware.approval.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.approval.dto.ApprovalFormDto;
import com.mjc.groupware.approval.service.ApprovalService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {
	
	private Logger logger = LoggerFactory.getLogger(ApprovalController.class);
	
	private final ApprovalService service;
	
	// 관리자
	
	// 관리자만 접근 가능한 url
	@GetMapping("/admin/approval")
	public String approvalAdminView() {
		return "/approval/admin/approvalManagement";
	}
	
	@GetMapping("/admin/approval/create")
	public String createApprovalAdminView() {
		return "/approval/admin/createApproval";
	}
	
	@PostMapping("/admin/approval/create")
	@ResponseBody
	public Map<String,String> createApprovalApi(ApprovalFormDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "결재 양식 생성 실패");

		logger.info("진입 test");
		
		int result = service.createApprovalApi(dto);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "결재 양식 생성 성공");
		}
		
		return resultMap;
	}
	
	
	
	// 사용자
	
	// 인증받은 모든 사원이 접근 가능한 url
	@GetMapping("/approval")
	public String approvalView() {
		return "/approval/user/approval";
	}
	
}
