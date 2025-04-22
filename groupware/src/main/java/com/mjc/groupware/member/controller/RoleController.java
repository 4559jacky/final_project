package com.mjc.groupware.member.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mjc.groupware.member.dto.RoleDto;
import com.mjc.groupware.member.service.RoleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RoleController {
	
	private Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	private final RoleService service;
	
	@PostMapping("/admin/role/create")
	public String createRole(@ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {
		logger.info("RoleDto: {}", roleDto);
		
		try {
			service.createRole(roleDto);
			
			redirectAttributes.addFlashAttribute("res_code", "200");
			redirectAttributes.addFlashAttribute("res_msg", "권한 추가가 성공적으로 완료되었습니다.");
		} catch(IllegalArgumentException e) {
			logger.error("권한 생성 실패", e);
			redirectAttributes.addFlashAttribute("res_code", "400");
		    redirectAttributes.addFlashAttribute("res_msg", "이미 사용 중인 권한 코드입니다.");
		} catch(Exception e) {
			logger.error("권한 생성 실패", e);
			redirectAttributes.addFlashAttribute("res_code", "500");
		    redirectAttributes.addFlashAttribute("res_msg", "권한 추가 중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return "redirect:/admin/company";
	}
	
	@PostMapping("/admin/role/update")
	public String updateRole(@ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {
		logger.info("RoleDto: {}", roleDto);
		
		try {
			service.updateRole(roleDto);
			
			redirectAttributes.addFlashAttribute("res_code", "200");
			redirectAttributes.addFlashAttribute("res_msg", "권한 수정이 성공적으로 완료되었습니다.");
		} catch(IllegalArgumentException e) {
			logger.error("권한 생성 실패", e);
			redirectAttributes.addFlashAttribute("res_code", "400");
		    redirectAttributes.addFlashAttribute("res_msg", "이미 사용 중인 권한 코드입니다.");
		} catch(Exception e) {
			logger.error("권한 생성 실패", e);
			redirectAttributes.addFlashAttribute("res_code", "500");
		    redirectAttributes.addFlashAttribute("res_msg", "권한 수정 중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return "redirect:/admin/company";
	}
	
	@PostMapping("/admin/role/delete")
	public String deleteRole(@ModelAttribute RoleDto roleDto, RedirectAttributes redirectAttributes) {
		logger.info("RoleDto: {}", roleDto);
		
		try {
			service.deleteRole(roleDto);
			
			redirectAttributes.addFlashAttribute("res_code", "200");
			redirectAttributes.addFlashAttribute("res_msg", "권한 삭제가 성공적으로 완료되었습니다.");
		} catch(Exception e) {
			logger.error("권한 삭제 실패", e);
			redirectAttributes.addFlashAttribute("res_code", "500");
		    redirectAttributes.addFlashAttribute("res_msg", "권한 삭제 중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return "redirect:/admin/company";
	}
	
}
