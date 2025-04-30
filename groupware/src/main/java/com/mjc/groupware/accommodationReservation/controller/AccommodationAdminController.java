package com.mjc.groupware.accommodationReservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.mjc.groupware.common.annotation.CheckPermission;

@Controller
public class AccommodationAdminController {
	
	// HTML리턴(adminHome 페이지로 이동)
	@CheckPermission("WELFARE_ADMIN")
	@GetMapping("/adminHome")
	public String adminHomeView() {
		return "accommodation/adminHome";
	}
		
	// HTML리턴(adminCreate 페이지로 이동)
	@CheckPermission("WELFARE_ADMIN")
	@GetMapping("/admin/create")
	public String adminCreateView() {
		return "accommodation/adminCreate";
	}
	
	// HTML리턴(adminList 페이지로 이동)
	@GetMapping("/adminList")
	public String adminListView() {
		return "accommodation/adminList";
	}
	
	
}
