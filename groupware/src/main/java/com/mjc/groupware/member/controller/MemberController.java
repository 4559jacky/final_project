package com.mjc.groupware.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private static int cnt = 0;
	private final MemberService service;
	
	@GetMapping("/login")
	public String loginView(
			@RequestParam(value="error", required=false) String error,
			@RequestParam(value="errorMsg", required=false) String errorMsg,
			Model model) {
		
		if(error == null) {
			cnt = 0;
		} else {
			cnt++;
		}
		
		model.addAttribute("error", error);
		model.addAttribute("errorMsg", errorMsg);
		
		System.out.println("MemberController - 로그인 연속 실패 횟수 : "+cnt+"회");
		
		return "member/login";
	}
	
	@GetMapping("/admin/member/create")
	public String createMemberView() {
		return "member/create";
	}
	
	@PostMapping("/admin/member/create")
	@ResponseBody
	public Map<String, String> createMemberApi(MemberDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "사원 등록 도중 알 수 없는 오류가 발생하였습니다.");
		
		Member member = service.createMember(dto);
		
		if(member != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "사원 등록이 성공적으로 완료되었습니다.");
		}
		
		return resultMap;
	}
	
}
