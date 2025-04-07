package com.mjc.groupware.home.comtroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	// 브랜치 테스트 ysw
	
	@GetMapping({"", "/", "/home"})
	public String homeView() {
		return "home";
	}
	
	@GetMapping("/starter")
	public String starterView() {
		return "starter";
	}
	
	@GetMapping("/sample")
	public String sampleView() {
		return "sample";
	}
	
}
