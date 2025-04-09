package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.service.SharedService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class SharedController {
	
	private Logger logger = LoggerFactory.getLogger(SharedController.class);

	private final SharedService service;
	
	@GetMapping("/admin/shared")
	public String listView(Model model) {
		model.addAttribute("sharedList",service.getSharedList());
		return "/shared/admin/list";
	}
	
	
	@GetMapping("/admin/shared/create")
	public String createSharedAdminView() {
		return "/shared/admin/create";
	}
	
	@PostMapping("/admin/shared/create")
	@ResponseBody
	public Map<String,String> createSharedApi(SharedDto dto){
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "결재 양식 생성 실패");

		logger.info("진입 test");
		
		int result = service.createSharedApi(dto);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "결재 양식 생성 성공");
		}
		
		return resultMap;
	}
}
