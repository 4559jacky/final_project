package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedDto;
import com.mjc.groupware.shared.entity.Shared;
import com.mjc.groupware.shared.repository.SharedRepository;
import com.mjc.groupware.shared.service.SharedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SharedController {

    private final SharedService service;
    private final SharedRepository repository;

    // 게시글 목록 화면 + 게시글 검색 기능 추가 + 게시글 정렬
    @GetMapping("/admin/shared")
    public String listView(@RequestParam(value = "keyword", required = false) String keyword, 
    					   @RequestParam(value = "sort", defaultValue = "desc") String sort,	
    					   Model model) {
    	List<Shared> sharedList = service.searchShared(keyword, sort);
        model.addAttribute("sharedList", sharedList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "/shared/admin/list";
    }

    // 게시글 작성 화면
    @GetMapping("/admin/shared/create")
    public String createSharedAdminView() {
        return "/shared/admin/create";
    }

    // 게시글 수정 화면
    @GetMapping("/admin/shared/update")
    public String updateSharedAdminView(@RequestParam("sharedNo") Long sharedNo, Model model) {
    	Shared shared = service.getSharedUpdate(sharedNo);
    	if(shared == null) {
    		return "redirect:/shared/admin";
    	}
    	model.addAttribute("shared",shared);
    	return "/shared/admin/update";
    }
    
    // 게시글 등록 처리 (fetch용)
    @PostMapping("/admin/shared/create")
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
    @GetMapping("/admin/shared/detail")
    public String detailView(@RequestParam("sharedNo") Long sharedNo, Model model) {
        Shared shared = service.getSharedDetail(sharedNo);
        if (shared == null) {
            // 게시글이 없는 경우 처리 (예: 목록으로 리다이렉트)
            return "redirect:/admin/shared";
        }
        model.addAttribute("shared", shared);
        return "/shared/admin/detail";
    }
// 게시글 수정 화면
    @PostMapping("/admin/shared/update")
    @ResponseBody
    public Map<String, String> updateSharedApi(@ModelAttribute SharedDto dto) {
    	Map<String, String> result = new HashMap<>();
    	result.put("res_code", "500");
    	result.put("res_msg", "수정 실패");
    	
    	String content = dto.getShared_content()
    			.replaceAll(",<p>","" )
    			.replaceAll("</p>", "")
    			.replaceAll("<p>", "")
    			.trim();
    	dto.setShared_content(content);
    	
    	try {
    		int updateResult = service.updateShared(dto);
    		if(updateResult > 0) {
    			result.put("res_code", "200");
    			result.put("res_msg", "수정 성공");
    		}
    	}catch(Exception e) {
    		result.put("res_msg", "오류");
    	}
    	return result;
    }
    
  //게시글 삭제
  //RedirectAttributes => addFlashAttribute 1회성, redirect이후 한번만 유지되고, 자동 삭제.
    @GetMapping("/admin/shared/delete")
    public String deleteShared(@RequestParam("sharedNo") Long sharedNo, RedirectAttributes msg) {
    	service.deleteShared(sharedNo);
    	msg.addFlashAttribute("message","삭제가 완료되었습니다!");
    	return "redirect:/admin/shared";
    

	}
 
   
}