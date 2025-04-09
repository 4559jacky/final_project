package com.mjc.groupware.pos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.pos.dto.PosDto;
import com.mjc.groupware.pos.dto.PosOrderDto;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.service.PosService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PosController {
	
	private Logger logger = LoggerFactory.getLogger(PosController.class);
	
	private final PosService service;
	
	@GetMapping("/admin/pos/create")
	public String createPosView(Model model) {
		List<Pos> posList = service.selectPosAllByPosOrderAsc();
		
		if(!posList.isEmpty()) {
			model.addAttribute("posList", posList);
		}

		return "pos/create";
	}
	
	@PostMapping("/admin/pos/create")
	@ResponseBody
	public Map<String, String> createPosApi(PosDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "직급 등록 도중 알 수 없는 오류가 발생하였습니다.");
		
		logger.info("PosDto : {}", dto);
		
		try {
			service.createPos(dto);
			
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "직급 등록이 성공적으로 완료되었습니다.");
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("직급 등록 중 오류 발생", e);
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "직급 등록 도중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/admin/pos/update/order")
	@ResponseBody
	public Map<String, String> updatePosOrderApi(@RequestBody List<PosOrderDto> posOrderList) {
		Map<String, String> resultMap = new HashMap<>();
	    
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "직급 순서 변경 도중 알 수 없는 오류가 발생하였습니다.");
		
		logger.info("List<PosOrderDto> : {}", posOrderList);
		
		try {
	        if (posOrderList == null || posOrderList.isEmpty()) {
	            resultMap.put("res_code", "400");
	            resultMap.put("res_msg", "변경할 순서 정보가 없습니다.");
	            
	            return resultMap;
	        }

	        service.updateOrder(posOrderList);

	        resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "직급 순서가 변경되었습니다.");
	    } catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("직급 순서 변경 중 오류 발생", e);
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "직급 순서 변경 도중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/admin/pos/update/name")
	@ResponseBody
	public Map<String, String> updatePosNameApi(@RequestBody PosDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "직급명 변경 도중 알 수 없는 오류가 발생하였습니다.");
		
		logger.info("PosDto : {}", dto);

		try {
			service.updateName(dto);
			
			resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "직급명이 변경되었습니다.");	
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch (DataIntegrityViolationException e) {
	        resultMap.put("res_code", "409");
	        resultMap.put("res_msg", "이미 존재하는 직급명입니다.");
		} catch(Exception e) {
			logger.error("직급명 변경 중 오류 발생", e);
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "직급명 변경 도중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/admin/pos/delete")
	@ResponseBody
	public Map<String, String> deletePosApi(@RequestBody PosDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "직급 삭제 도중 알 수 없는 오류가 발생하였습니다.");
		
		logger.info("PosDto : {}", dto);
		
		try {
			service.deletePos(dto);
			
			resultMap.put("res_code", "200");
	        resultMap.put("res_msg", "직급이 삭제되었습니다.");	
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("직급 삭제 중 오류 발생", e);
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "직급 삭제 도중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return resultMap;
	}
	
}
