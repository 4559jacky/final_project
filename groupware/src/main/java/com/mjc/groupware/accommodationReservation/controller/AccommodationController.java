package com.mjc.groupware.accommodationReservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccommodationController {

	// HTML리턴(home 페이지로 이동)
	@GetMapping("/accommodation")
	public String showHomeView() {
		return "accommodation/home";
	}
	
	//
//	@GetMapping("/accommodation/detail")
//	public String showDetailView() {
//		return "accommodation/detail";
//	}

	@GetMapping("/accommodation/list")
	public String showListView() {
		return "accommodation/list";
	}
	
}
