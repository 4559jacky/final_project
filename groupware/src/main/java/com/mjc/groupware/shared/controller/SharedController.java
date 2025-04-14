package com.mjc.groupware.shared.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedController {
	@GetMapping("/shared")
	public String mainView() {
		return "/shared/main";
	}
}
