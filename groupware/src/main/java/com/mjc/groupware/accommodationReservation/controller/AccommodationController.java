package com.mjc.groupware.accommodationReservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccommodationController {

	// HTML리턴(home 페이지로 이동)
	@GetMapping("/accommodation")
	public String accommodationView() {
		return "accommodation/home";
	}
	
	// HTML리턴(adminHome 페이지로 이동)
	@GetMapping("/adminHome")
	public String adminHomeView() {
		return "accommodation/adminHome";
	}
	
	// 
	@GetMapping("/admin/create")
	public String adminCreateView() {
	    return "accommodation/adminCreate";
	}

	
}
