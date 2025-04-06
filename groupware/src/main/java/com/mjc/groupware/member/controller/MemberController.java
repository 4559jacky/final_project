package com.mjc.groupware.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private static int cnt = 0;
	
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
	
	@GetMapping("/member/create")
	public String createMemberView() {
		return "member/create";
	}
	
}
