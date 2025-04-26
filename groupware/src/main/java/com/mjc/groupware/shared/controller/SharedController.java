package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Shared;
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
    @ResponseBody
    public Map<String, String> createApprovalApi(
            @ModelAttribute SharedDto dto,
            @AuthenticationPrincipal MemberDetails memberDetails) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 생성 실패");

        // 로그인한 사용자 확인
        if (memberDetails == null) {
            resultMap.put("res_msg", "로그인한 사용자를 찾을 수 없습니다.");
            return resultMap;
        }

        // HTML에서 넘어온 member_no를 사용 (이미 SharedDto에 바인딩됨)
        if (dto.getMember_no() == null) {
            resultMap.put("res_msg", "회원 번호를 찾을 수 없습니다.");
            return resultMap;
        }

        int result = service.createSharedApi(dto);

        if (result > 0) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글 생성 성공");
        }

        return resultMap;
    }
    
 // 게시글 상세 화면
    @GetMapping("/detail")
    public String detailView(@RequestParam("sharedNo") Long sharedNo, Model model) {
        Shared shared = service.getSharedDetail(sharedNo);
        if (shared == null) {
            // 게시글이 없는 경우 처리 (예: 목록으로 리다이렉트)
            return "redirect:/admin/shared";
        }
        model.addAttribute("shared", shared);
        return "/shared/admin/detail";
    }
}