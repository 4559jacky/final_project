package com.mjc.groupware.pos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.pos.dto.PosDto;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.service.PosService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PosController {
	
	private final PosService service;
	
	@GetMapping("/admin/pos/create")
	public String createPositionView(Model model) {
		List<Pos> posList = service.selectPosAll();
		
		if(!posList.isEmpty()) {
			model.addAttribute("posList", posList);
		}

		return "pos/create";
	}
	
	@PostMapping("/admin/pos/create")
	@ResponseBody
	public Map<String, String> CreatePositionApi(PosDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		try {
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "직급 등록 도중 알 수 없는 오류가 발생하였습니다.");
			
			Pos pos = service.createPos(dto);
			
			if(pos != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "직급 등록이 성공적으로 완료되었습니다.");
			}
			
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		}
		
		return resultMap;
	}
	
}
