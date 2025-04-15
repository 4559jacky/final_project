package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.shared.dto.SharedFileDto;
import com.mjc.groupware.shared.service.FileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SharedFileController {
	
	private final FileService fileService;
	
	
	// 공유문서함 메인화면 view
	@GetMapping("/shared")
	public String mainView() {
		return "/shared/main";
	}
	
	@PostMapping("/shared/file/upload")
	public Map<String,String> uploadFile(@RequestParam("file") MultipartFile file,
							 @AuthenticationPrincipal(expression = "member") Member member) {
		Map<String,String> result = new HashMap<>();
		result.put("res_code", "500");
		result.put("res_msg", "파일 업로드 실패");
		
		try {
	        // 서비스에 파일과 사용자 정보를 넘겨 저장 처리
	        SharedFileDto uploaded = fileService.uploadFile(file, member);

	        if (uploaded != null) {
	            result.put("res_code", "200");
	            result.put("res_msg", "파일 업로드 성공!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("res_msg", "서버 오류 발생!");
	    }

	    return result; 
		
		
		
	}
}
