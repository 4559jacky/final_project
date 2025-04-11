package com.mjc.groupware.dept.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.dept.dto.DeptTreeDto;
import com.mjc.groupware.dept.service.DeptTreeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeptTreeController {
	
	private final DeptTreeService service;
	
	@GetMapping("/admin/dept/tree")
	@ResponseBody
	public List<DeptTreeDto> getDeptTree() {
		
	    return service.getDeptTree();
	}
	
	@GetMapping("/dept/tree")
	@ResponseBody
	public List<DeptTreeDto> getDeptTreeUser() {
	    return service.getDeptTree();
	}
	
}
