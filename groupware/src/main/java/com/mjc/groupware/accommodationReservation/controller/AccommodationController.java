package com.mjc.groupware.accommodationReservation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.service.AccommodationAttachService;
import com.mjc.groupware.accommodationReservation.service.AccommodationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccommodationController {

	private final AccommodationService accommodationService;
	private final AccommodationAttachService accommodationAttachService;
	
	// HTML리턴(home 페이지로 이동)
	@GetMapping("/accommodation")
	public String showHomeView() {
		return "accommodation/home";
	}

	@GetMapping("/accommodation/list")
	public String showListView() {
		return "accommodation/list";
	}
	
//	@GetMapping("/accommodation")
//	public String homeView(Model model) {
//	    List<AccommodationInfo> accommodationsHomeList = accommodationService.getAllAccommodations();
//	    System.out.println("accommodationsHomeList"+accommodationsHomeList);
//	    model.addAttribute("accommodationsHomeList", accommodationsHomeList);
//	    return "accommodation/detail"; // → Home.html 위치에 따라 수정
//	}
	
	
}
