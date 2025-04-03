package com.mjc.groupware.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping({"", "/", "/home"})
	public String homeView() {
		return "home";
	}
	
	@GetMapping("/sample")
	public String sampleView() {
		return "sample";
	}
	
}
