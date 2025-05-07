package com.mjc.groupware.accommodationReservation.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.accommodationReservation.service.AccommodationAttachService;
import com.mjc.groupware.accommodationReservation.service.AccommodationService;
import com.mjc.groupware.board.controller.BoardAttachController;
import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.notice.dto.NoticeDto;

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

	// 숙소 수정
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

	        // 파일 처리 (선택)
	        if (files != null && !files.isEmpty()) {
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

	// 숙소 상세
	@GetMapping("/accommodation/detail/{accommodationNo}")
	public String viewAccommodation(@PathVariable("accommodationNo") Long accommodationNo, Model model) {
	    AccommodationInfoDto dto = accommodationService.findById(accommodationNo);
	    if (dto == null) {
	        return "redirect:/accommodation/adminHome";
	    }

	    List<AccommodationAttachDto> attachList = accommodationService.findAttachList(accommodationNo); // 이미지 리스트
	    System.out.println("파일 정보: " + attachList);
	    
	    model.addAttribute("accommodation", dto);
	    model.addAttribute("attachList", attachList); // 이미지 리스트 전달
	    return "accommodation/detail"; // detail.html로 이동
	}

	// 숙소 삭제
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
