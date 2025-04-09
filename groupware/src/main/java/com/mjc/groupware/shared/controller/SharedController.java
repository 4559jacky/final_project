package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartException;

import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.service.SharedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shared")
@Slf4j
public class SharedController {

	private final SharedService service;

	// 게시글 목록 화면
	@GetMapping("")
	public String listView(Model model) {
		model.addAttribute("sharedList", service.getSharedList());
		return "/shared/admin/list";
	}

	// 게시글 작성 화면
	@GetMapping("/create")
	public String createSharedAdminView() {
		return "/shared/admin/create";
	}

	// 게시글 등록 처리 (fetch용)
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createShared(
			@ModelAttribute SharedDto sharedDto,
			@AuthenticationPrincipal MemberDetails memberDetails) {

		Map<String, Object> result = new HashMap<>();

		try {
			// 로그인 사용자 설정
			sharedDto.setMember_no(memberDetails.getMember().getMemberNo());

			// 저장
			service.saveShared(sharedDto);

			result.put("res_code", "200");
			result.put("res_msg", "게시글이 성공적으로 작성되었습니다.");
		} catch (MultipartException e) {
			result.put("res_code", "400");
			result.put("res_msg", "파일 업로드 실패: " + e.getMessage());
		} catch (Exception e) {
			result.put("res_code", "500");
			result.put("res_msg", "게시글 작성 중 오류 발생");
		}

		return ResponseEntity.ok(result);
	}
}
