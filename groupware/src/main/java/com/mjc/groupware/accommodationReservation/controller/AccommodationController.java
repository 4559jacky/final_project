package com.mjc.groupware.accommodationReservation.controller;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class AccommodationController {

	// HTML리턴(페이지 이동용)
	@GetMapping("/accommodation")
	public String accommodationView(Model model) {
		return "accommodation/accommodation";
	}
	
	
}
