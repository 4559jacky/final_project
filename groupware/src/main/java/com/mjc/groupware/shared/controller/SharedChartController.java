package com.mjc.groupware.shared.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.shared.service.SharedChartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SharedChartController {
	
	private final SharedChartService sharedChartService;
	
	@GetMapping("/shared/trash/usage")
	 public Map<String, Long> getUsageStats() {
        return sharedChartService.getUsageStats();
    }
	

}
