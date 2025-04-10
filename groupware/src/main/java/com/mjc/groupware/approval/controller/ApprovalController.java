package com.mjc.groupware.approval.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.approval.dto.ApprovalFormDto;
import com.mjc.groupware.approval.entity.ApprovalForm;
import com.mjc.groupware.approval.service.ApprovalService;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ApprovalController {

    private final WebSecurityCustomizer configure;
	
	private Logger logger = LoggerFactory.getLogger(ApprovalController.class);
	
	private final ApprovalService service;
	private final MemberService memberService;
	private final DeptService deptService;

	// 관리자 : 관리자만 접근 가능한 url
	
	@GetMapping("/admin/approval")
	public String approvalAdminView(Model model) {
		
		List<ApprovalForm> resultList = service.selectApprovalFormAll();
		model.addAttribute("formList", resultList);
		return "/approval/admin/approvalManagement";
	}
	
	// 결재 양식 생성 페이지로 이동
	@GetMapping("/admin/approvalForm/create")
	public String createApprovalAdminView() {
		return "/approval/admin/createApprovalForm";
	}
	
	// 결재 양식 생성
	@PostMapping("/admin/approvalForm/create")
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
	
	// 결재 양식 선택
	@PostMapping("/admin/approvalForm/view/{id}")
	@ResponseBody
	public ApprovalFormDto approvalFormView(@PathVariable("id") Long id) {
	    ApprovalFormDto dto = service.selectApprovalFormById(id);
	    return dto;
	}
	
	// 결재 양식 수정 페이지로 이동
	@GetMapping("/admin/approvalForm/update/{id}")
	public String updateApprovalFormView(@PathVariable("id") Long id, Model model) {
	    ApprovalFormDto dto = service.selectApprovalFormById(id);
	    model.addAttribute("form", dto);
	    return "/approval/admin/updateApprovalForm";
	}
	
	
	// 결재 양식 수정
	@PostMapping("/admin/approvalForm/update")
	@ResponseBody
	public Map<String,String> updateApprovalForm(ApprovalFormDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "결재 양식 수정 실패");

		int result = service.createApprovalApi(dto);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "결재 양식 수정 성공");
		}
		
		return resultMap;
		
	}
	
	// 결재 양식 상태 변경
	@PostMapping("/admin/approvalForm/statusUpdate/{id}")
	@ResponseBody
	public Map<String,String> updateApprovalFormStatus(@PathVariable("id") Long id) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "결재 양식 상태변경 실패");

		int result = service.updateApprovalFormStatus(id);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "결재 양식 상태변경 성공");
		}
		
		return resultMap;
	}
	
	
	
	// 사용자 : 인증받은 모든 사원이 접근 가능한 url
	
	@GetMapping("/approval")
	public String approvalView() {
		return "/approval/user/approval";
	}
	
	@GetMapping("/approval/receive")
	public String receiveApprovalView() {
		return "/approval/user/receiveApproval";
	}
	
	@GetMapping("/approval/create")
	public String createApprovalView(Model model) {
		List<ApprovalForm> resultList = service.selectApprovalFormAll();
		model.addAttribute("formList", resultList);
		return "/approval/user/createApproval";
	}
	
	// 결재 양식 선택
	@PostMapping("/approvalForm/view/{id}")
	@ResponseBody
	public ApprovalFormDto UserApprovalFormView(@PathVariable("id") Long id) {
	    ApprovalFormDto dto = service.selectApprovalFormById(id);
	    return dto;
	}
	
	@PostMapping("/approval/create/{id}")
	@ResponseBody
	public Map<String,Object> createApprovalDiv(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
		String userId = userDetails.getUsername();

	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member entity = memberService.selectMemberOne(memberDto);
	    MemberDto member = new MemberDto().toDto(entity);
	    
	    ApprovalFormDto dto = service.selectApprovalFormById(id);
	    
	    Map<String, Object> result = new HashMap<>();
	    result.put("approvalForm", dto);
	    result.put("member", member);
	    

	    return result;
	}
	
	// Dept dept = deptService.selectDeptAll();
	
	
	
	
}
