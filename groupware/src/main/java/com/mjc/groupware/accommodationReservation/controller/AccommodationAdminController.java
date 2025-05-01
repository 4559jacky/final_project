package com.mjc.groupware.accommodationReservation.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.service.AccommodationService;
import com.mjc.groupware.common.annotation.CheckPermission;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccommodationAdminController {
	
	private final AccommodationService accommodationService;
	
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
	
	@PostMapping("/register")
	public String registerAccommodation(
	        @RequestParam("name") String name,
	        @RequestParam("type") String type,
	        @RequestParam("address") String address,
	        @RequestParam("phone") String phone,
	        @RequestParam("email") String email,
	        @RequestParam("site") String site,
	        @RequestParam("price") String price,
	        @RequestParam("content") String content,
	        @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

		accommodationService.saveAccommodation(name, type, address, phone, email, site, price, content, imageFile);
	    return "redirect:/partner/list";
	}

	
	
}
