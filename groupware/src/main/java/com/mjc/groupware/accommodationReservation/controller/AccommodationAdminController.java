package com.mjc.groupware.accommodationReservation.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
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
	public String adminHomeView(Model model) {
		List<AccommodationInfo> accommodations = accommodationService.getAllAccommodations();
	    model.addAttribute("accommodations", accommodations);
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
	
	// 숙소등록
	@PostMapping("/accommodation/register")
	@ResponseBody
	public Map<String, Object> registerAccommodation(
	    @RequestParam("accommodation_type") String type,
	    @RequestParam("accommodation_name") String name,
	    @RequestParam("accommodation_content") String content,
	    @RequestParam("accommodation_address") String address,
	    @RequestParam("accommodation_phone") String phone,
	    @RequestParam("accommodation_email") String email,
	    @RequestParam("accommodation_site") String site,
	    @RequestParam("room_price") Long price,
	    @RequestParam("accommodation_no") Long accommodationNo,
	    @RequestParam("room_count") Long roomCount,
	    @RequestParam("accommodation_location") String location,
	    @RequestParam("reg_date") String regDate,
	    @RequestParam("mod_date") String modDate
	) {
		AccommodationInfoDto dto = AccommodationInfoDto.builder()
		        .accommodation_no(accommodationNo)
		        .accommodation_type(type)
		        .accommodation_name(name)
		        .accommodation_content(content)
		        .accommodation_address(address)
		        .accommodation_phone(phone)
		        .accommodation_email(email)
		        .accommodation_site(site)
		        .room_price(price)
		        .room_count(roomCount)
		        .accommodation_location(location)
		        .reg_date(LocalDateTime.parse(regDate))
		        .mod_date(LocalDateTime.parse(modDate))
		        .build();

		    accommodationService.register(dto);
	    // 저장 로직 구현
	    Map<String, Object> result = new HashMap<>();
	    result.put("res_code", "200");
	    result.put("res_msg", "숙소 등록 성공");
	    return result;
	}

	//
	
	
	
	
}
