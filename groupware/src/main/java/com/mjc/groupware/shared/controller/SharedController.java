package com.mjc.groupware.shared.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.shared.dto.AttachDto;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.service.AttachService;
import com.mjc.groupware.shared.service.SharedService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class SharedController {
	
	private Logger logger = LoggerFactory.getLogger(SharedController.class);

	private final SharedService service;
	private final AttachService attachService;
	
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
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "게시글 등록 실패");

		List<AttachDto> attachDtoList = new ArrayList<AttachDto>();
		
		for(MultipartFile mf : dto.getFiles()) {
			AttachDto attachDto = attachService.uploadFile(mf);
			if(attachDto != null) attachDtoList.add(attachDto);
		}
		
		if(dto.getFiles().size() == attachDtoList.size()) {
			int result = service.createSharedApi(dto,attachDtoList);
			if(result > 0) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "게시글 등록되었습니다.");
			}
		}
		
		return resultMap;
	}
}
