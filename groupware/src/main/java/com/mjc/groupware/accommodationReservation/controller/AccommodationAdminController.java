package com.mjc.groupware.accommodationReservation.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.service.AccommodationAttachService;
import com.mjc.groupware.accommodationReservation.service.AccommodationService;
import com.mjc.groupware.common.annotation.CheckPermission;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccommodationAdminController {
	
	private final AccommodationService accommodationService;
	private final AccommodationAttachService accommodationAttachService;
	
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
		@ModelAttribute AccommodationInfoDto dto,
		@RequestParam(value = "files", required = false) List<MultipartFile> files
	) {
		 Map<String, Object> result = new HashMap<>();
		    try {
		        dto.setReg_date(LocalDateTime.now());
		        dto.setMod_date(LocalDateTime.now());

		        AccommodationInfo savedInfo = accommodationService.register(dto);

		     // 파일 저장
		        if (files != null && !files.isEmpty()) {
		            System.out.println("업로드된 파일 수: " + files.size());
		            for (MultipartFile mf : files) {
		                if (!mf.isEmpty()) {
		                    AccommodationAttachDto attachDto = accommodationAttachService.uploadFile(mf);
		                    if (attachDto != null) {
		                        accommodationAttachService.saveAttach(attachDto, savedInfo);
		                    }
		                }
		            }
		        }

		        result.put("res_code", "200");
		        result.put("res_msg", "숙소 등록 성공");
		    } catch (Exception e) {
		        e.printStackTrace();
		        result.put("res_code", "500");
		        result.put("res_msg", "등록 중 오류 발생");
		    }
		    return result;
	}



	
	
	
	
}
