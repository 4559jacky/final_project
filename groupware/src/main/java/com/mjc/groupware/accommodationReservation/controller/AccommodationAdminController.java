package com.mjc.groupware.accommodationReservation.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.accommodationReservation.dto.AccommodationAttachDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationInfoDto;
import com.mjc.groupware.accommodationReservation.dto.AccommodationReservationDto;
import com.mjc.groupware.accommodationReservation.dto.SearchDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.service.AccommodationAttachService;
import com.mjc.groupware.accommodationReservation.service.AccommodationReservationService;
import com.mjc.groupware.accommodationReservation.service.AccommodationService;
import com.mjc.groupware.address.service.AddressService;
import com.mjc.groupware.common.annotation.CheckPermission;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AccommodationAdminController {

	private final AccommodationService accommodationService;
	private final AccommodationAttachService accommodationAttachService;
	private final AddressService addressService;
	private final AccommodationReservationService reservationService; 
	
	// 관리자(adminHome 페이지로 이동)
	@CheckPermission("WELFARE_ADMIN")
	@GetMapping("/accommodation/adminHome")
	public String adminHomeView(Model model) {
		List<AccommodationInfoDto> accommodations = accommodationService.getAllAccommodations();
	    model.addAttribute("accommodations", accommodations);
		return "accommodation/adminHome";
	}
	
	// 관리자(adminCreate 페이지로 이동)
	@CheckPermission("WELFARE_ADMIN")
	@GetMapping("/admin/accommodation/create")
	public String adminCreateView(Model model) {
		List<String> addr1List = addressService.selectAddr1Distinct();
		model.addAttribute("addr1List", addr1List);
		model.addAttribute("selectedAddr1", null);
		model.addAttribute("addr2List", List.of());
		model.addAttribute("selectedAddr2", null);
		
		return "accommodation/adminCreate";
	}
	
	// 관리자(adminList 페이지로 이동)
	@GetMapping("/adminList")
	public String adminListView() {
		return "accommodation/adminList";
	}
	
	// 사용자(nav바 사내복지 클릭시 home.html 페이지로 이동/조회)
//	@GetMapping("/accommodation")
//	public String showHomeView(Model model) {
//		List<AccommodationInfoDto> list = accommodationService.showHomeView();
//		model.addAttribute("accommodationList", list);
//		return "accommodation/home";
//	}
	
	// home화면 필터링
	@GetMapping("/accommodation")
	public String showHomeView(@ModelAttribute SearchDto searchDto, Model model) {
	    List<AccommodationInfoDto> resultList = accommodationService.getFilteredList(searchDto);
	    model.addAttribute("accommodationList", resultList);
	    model.addAttribute("searchDto", searchDto);
	    return "accommodation/home";
	}

	// 사용자(list 페이지로 이동)
	@GetMapping("/accommodation/list")
	public String showListView() {
		return "accommodation/list";
	}
	
	// 숙소등록(관리자)
	@PostMapping("/accommodation/register")
	@ResponseBody
	public Map<String, Object> registerAccommodation(
		@ModelAttribute AccommodationInfoDto dto,
		@RequestParam(value = "files", required = false) List<MultipartFile> files
	) {
		System.out.println(dto.getAccommodation_no());
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
		        result.put("res_msg", "숙소가 등록되었습니다.");
		    } catch (Exception e) {
		        e.printStackTrace();
		        result.put("res_code", "500");
		        result.put("res_msg", "숙소 등록 중 오류가 발생했습니다.");
		    }
		    return result;
	}
	
	// 숙소 상세(관리자)
	@GetMapping("/admin/accommodation/detail/{accommodationNo}")
	public String viewAccommodation(@PathVariable("accommodationNo") Long accommodationNo, Model model) {
		AccommodationInfoDto dto = accommodationService.findById(accommodationNo);
		if (dto == null) {
		    return "redirect:/accommodation/adminHome";
		}

		List<AccommodationAttachDto> attachList = accommodationService.findAttachList(accommodationNo); // 이미지 리스트
		
		AccommodationReservationDto reservationDto = reservationService.findLatestByAccommodationNo(accommodationNo);
		model.addAttribute("accommodation", dto);
		model.addAttribute("attachList", attachList); // 이미지 리스트 전달
		model.addAttribute("reservation", reservationDto);
		    
		return "accommodation/adminDetail";
	}
	
	// 숙소 상세(사용자)
	@GetMapping("/accommodation/userHome/{accommodationNo}")
	public String viewAccommodationUser(@PathVariable("accommodationNo") Long accommodationNo, Model model) {
		AccommodationInfoDto dto = accommodationService.findById(accommodationNo);

		List<AccommodationAttachDto> attachList = accommodationService.findAttachList(accommodationNo);
		model.addAttribute("accommodation", dto);
		model.addAttribute("attachList", attachList);
		    
		return "accommodation/detail";
	}

	// 숙소 수정(관리자)
	@PostMapping("/accommodation/update/{accommodationNo}")
	@ResponseBody
	public Map<String, Object> updateAccommodation(
		@PathVariable("accommodationNo") Long accommodationNo,
	    @ModelAttribute AccommodationInfoDto dto,
	    @RequestParam(value = "files", required = false) List<MultipartFile> files
	) {
		
	    Map<String, Object> result = new HashMap<>();
	    try {
	        dto.setAccommodation_no(accommodationNo); // id 지정
	        dto.setMod_date(LocalDateTime.now());

	        AccommodationInfo updated = accommodationService.update(dto); // 서비스 호출

            // 기존 첨부파일 전체 삭제
	        accommodationAttachService.deleteAllByAccommodation(accommodationNo);
	        
	        // 새 파일이 있는 경우만 기존 파일 삭제 + 새로운 파일 저장
	        if (files != null && !files.isEmpty()) {
	            accommodationAttachService.deleteAllByAccommodation(accommodationNo);

	            for (MultipartFile mf : files) {
	                if (!mf.isEmpty()) {
	                    AccommodationAttachDto attachDto = accommodationAttachService.uploadFile(mf);
	                    if (attachDto != null) {
	                        accommodationAttachService.saveAttach(attachDto, updated);
	                    }
	                }
	            }
	        }

	        result.put("res_code", "200");
	        result.put("res_msg", "숙소 수정이 완료되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("res_code", "500");
	        result.put("res_msg", "숙소 수정 중 오류가 발생하였습니다.");
	    }

	    return result;
	}
	
	@GetMapping("/accommodation/update/{id}")
	public String updateAccommodation(@PathVariable("id") Long id, Model model) {
	    AccommodationInfoDto dto = accommodationService.findById(id);
	    if (dto == null) {
	        return "redirect:/adminHome";
	    }

	    List<AccommodationAttachDto> attachList = accommodationService.findAttachList(id);

	    model.addAttribute("accommodation", dto);
	    model.addAttribute("attachList", attachList);

	    return "accommodation/adminUpdate";  // 수정과 생성 페이지 공유
	}

	// 숙소 삭제(관리자)
	@DeleteMapping("/accommodation/delete/{accommodationNo}")
    @ResponseBody
    public Map<String, String> deleteAccommodation(@PathVariable("accommodationNo") Long accommodationNo) {
        Map<String, String> resultMap = new HashMap<>();
        try {
        	accommodationService.deleteAccommodation(accommodationNo);
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "숙소가 삭제되었습니다.");
        } catch (Exception e) {
        	e.printStackTrace();
        	resultMap.put("res_code", "500");
        	resultMap.put("res_msg", "숙소 수정 중 오류가 발생하였습니다.");
        }
        return resultMap;
    }
	
	
	
	
}
