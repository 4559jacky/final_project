package com.mjc.groupware.address.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.address.service.AddressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AddressController {
	
	private final AddressService service;
	
	@GetMapping("/address/addr2")
	@ResponseBody
	public List<String> selectAddr2AllByAddr1(@RequestParam("addr1") String addr1) {
		if (addr1 == null || addr1.trim().isEmpty()) {
	        throw new IllegalArgumentException("addr1 값이 잘못되었습니다.");
	    }

		List<String> addr2List = service.selectAddr2ByAddr1(addr1);
		
		return addr2List;
	}
	
}
