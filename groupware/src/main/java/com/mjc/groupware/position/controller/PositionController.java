package com.mjc.groupware.position.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.position.dto.PositionDto;

@Controller
public class PositionController {
	
	@GetMapping("/position/create")
	public String createPositionView() {
		return "position/create";
	}
	
	@PostMapping("/position/create")
	@ResponseBody
	public Map<String, String> CreatePositionApi(PositionDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		return resultMap;
	}
	
}
