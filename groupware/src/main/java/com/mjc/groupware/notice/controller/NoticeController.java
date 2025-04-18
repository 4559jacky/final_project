package com.mjc.groupware.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
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
import com.mjc.groupware.notice.dto.NoticeDto;
import com.mjc.groupware.notice.entity.Notice;
import com.mjc.groupware.notice.repository.NoticeRepository;
import com.mjc.groupware.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService service;
    private final NoticeRepository repository;

    // 게시글 목록 화면 + 게시글 검색 기능 추가 + 게시글 정렬 + 검색 조건+ 페이징
    @GetMapping("/notice")
    public String listView(
    					   @RequestParam(value = "search_type", required = false) Integer searchType,
    					   @RequestParam(value = "keyword", required = false) String keyword, 
    					   @RequestParam(value = "sort", defaultValue = "desc") String sort,	
    					   @RequestParam(value = "page", defaultValue = "0") int page,
    					   Model model) {    	
    	Page<Notice> noticeList = service.searchNotice(searchType, keyword, sort, page);
        model.addAttribute("search_type", searchType);
    	model.addAttribute("noticeList", noticeList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "/notice/list";
    }

    // 게시글 작성 화면
    @GetMapping("/notice/create")
    public String createNoticeAdminView() {
        return "/notice/create";
    }

    // 게시글 수정 화면
    @GetMapping("/notice/update")
    public String updateNoticeAdminView(@RequestParam("noticeNo") Long noticeNo, Model model) {
    	Notice notice = service.getNoticeUpdate(noticeNo);
    	if(notice == null) {
    		return "redirect:/shared";
    	}
    	model.addAttribute("notice",notice);
    	return "/notice/update";
    }
    
    // 게시글 등록 처리 (fetch용)
    @PostMapping("/notice/create")
    @ResponseBody
    public Map<String, String> createNoticeApi(
            @ModelAttribute NoticeDto dto,
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

        int result = service.createNoticeApi(dto);

        if (result > 0) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글 생성 성공");
        }

        return resultMap;
    }
    
 // 게시글 상세 화면
    @GetMapping("/notice/detail")
    public String detailView(@RequestParam("noticeNo") Long noticeNo, Model model) {
        Notice notice = service.getNoticeDetail(noticeNo);
        if (notice == null) {
            // 게시글이 없는 경우 처리 (예: 목록으로 리다이렉트)
            return "redirect:/notice";
        }
        model.addAttribute("notice", notice);
        return "/notice/detail";
    }
// 게시글 수정 화면
    @PostMapping("/notice/update")
    @ResponseBody
    public Map<String, String> updateNoticeApi(@ModelAttribute NoticeDto dto) {
    	Map<String, String> result = new HashMap<>();
    	result.put("res_code", "500");
    	result.put("res_msg", "수정 실패");
    	
    	String content = dto.getNotice_content()
    			.replaceAll(",<p>","" )
    			.replaceAll("</p>", "")
    			.replaceAll("<p>", "")
    			.trim();
    	dto.setNotice_content(content);
    	
    	try {
    		int updateResult = service.updateNotice(dto);
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
    @GetMapping("/notice/delete")
    public String deleteNotice(@RequestParam("noticeNo") Long noticeNo, RedirectAttributes msg) {
    	service.deleteNotice(noticeNo);
    	msg.addFlashAttribute("message","삭제가 완료되었습니다!");
    	return "redirect:/notice";
    

	}
    
}