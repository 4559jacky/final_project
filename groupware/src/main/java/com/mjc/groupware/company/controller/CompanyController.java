package com.mjc.groupware.company.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.company.CompanyDto;

@Controller
public class CompanyController {
	
	private Logger logger = LoggerFactory.getLogger(CompanyController.class);
	
	@GetMapping("/admin/company/update")
	public String updateCompanyView() {
		
		return "/company/update";
	}
	
	@PostMapping("/admin/company/update")
	@ResponseBody
	public Map<String, String> updateCompanyApi(CompanyDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		logger.info("CompanyDto: {}", dto);
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "프로필 수정 중 알 수 없는 오류가 발생하였습니다");
		
		
		
		
		return resultMap;
	}
	
}
