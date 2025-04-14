package com.mjc.groupware.company.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.company.dto.CompanyDto;
import com.mjc.groupware.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CompanyController {
	
	private Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
	private final CompanyService service;
	
	@GetMapping("/admin/company/create")
	public String createCompanyView() {
		
		return "/company/create";
	}
	
	@PostMapping("/admin/company/create")
	@ResponseBody
	public Map<String, String> createCompanyApi(CompanyDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		logger.info("CompanyDto: {}", dto);
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "프로필 정보 수정 중 알 수 없는 오류가 발생하였습니다");
		
		try {
			MultipartFile file = dto.getProfile_image();
			
			CompanyDto param = service.uploadFile(file);
			param.setCompany_name(dto.getCompany_name());
			
			service.createCompany(param);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "프로필 정보가 수정되었습니다");
		} catch(Exception e) {
			logger.error("프로필 등록 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "프로필 정보 수정 중 알 수 없는 오류가 발생했습니다.");
		}

		return resultMap;
	}
	
}
