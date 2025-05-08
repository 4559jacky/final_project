package com.mjc.groupware.shared.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.service.SharedChartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SharedChartController {
	
	private final SharedChartService sharedChartService;
	
	@GetMapping("/shared/trash/usage")
	  public Map<String, Long> getUsageStats(@RequestParam String type, Authentication auth) {
        Member member = ((MemberDetails) auth.getPrincipal()).getMember();
        return sharedChartService.getUsageStats(type, member);
    }
	

}
