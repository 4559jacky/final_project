package com.mjc.groupware.notice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.notice.dto.NoticeDto;
import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.entity.Notice;
import com.mjc.groupware.notice.repository.AttachRepository;
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
    private final AttachRepository attachRepository;

    // 게시글 목록 화면 + 게시글 검색 기능 추가 + 게시글 정렬 + 검색 조건+ 페이징
    @CheckPermission("NOTICE_R")
    @GetMapping("/notice")
    public String listView(
    					   @RequestParam(value = "search_type", required = false) Integer searchType,
    					   @RequestParam(value = "keyword", required = false) String keyword, 
    					   @RequestParam(value = "sort", defaultValue = "desc") String sort,	
    					   @RequestParam(value = "page", defaultValue = "0") int page,
    					   Model model) {    	
    	// 검색 키워드에서 HTML 태그 제거
    	if (keyword != null) {
    		keyword = keyword.replaceAll("<[^>]*>", "").trim();
    		
    		// 빈값이 되면 검색 무시
    		if (keyword.isEmpty()) {
    			keyword = null;
    		}
    	}
    	Page<Notice> noticeList = service.searchNotice(searchType, keyword, sort, page);
        
    	
    	
    	// ✅ 고정글은 항상 상단 고정
    	List<Notice> fixedList = service.getFixedNotices();
    	model.addAttribute("fixedList", fixedList);
    	   
    	model.addAttribute("search_type", searchType);
    	model.addAttribute("noticeList", noticeList);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        return "notice/list";
    }

    // 게시글 작성 화면
    @CheckPermission("NOTICE_CRU")
    @GetMapping("/notice/create")
    public String createNoticeAdminView() {
        return "notice/create";
    }
    
    @CheckPermission("NOTICE_CRU")
    @GetMapping("/notice/update")
    public String updateNoticeAdminView(@RequestParam("noticeNo") Long noticeNo, @RequestParam("memberNo") Long memberNo, Model model) {
    	// 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if (authentication == null || !authentication.isAuthenticated()) {
    		throw new AccessDeniedException("로그인 정보가 없습니다.");
    	}
    	
    	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
    	Long currentMemberNo = memberDetails.getMember().getMemberNo();
    	
    	if (!memberNo.equals(currentMemberNo)) {
    		throw new AccessDeniedException("접근 권한이 없습니다.");
    	}
    	
        NoticeDto noticeDto = service.getNoticeUpdate(noticeNo);
        if (noticeDto == null) {
            return "redirect:/notice";
        }
        model.addAttribute("notice", noticeDto);
        return "notice/update";
    }
    
    // 게시글 등록 처리 (fetch용)
    @CheckPermission("NOTICE_CRU")
    @PostMapping("/notice/create")
    @ResponseBody
    public Map<String, String> createNoticeApi(
            @ModelAttribute NoticeDto dto,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetails memberDetails) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "500");
        resultMap.put("res_msg", "게시글 생성 실패");

        if (memberDetails == null) {
            resultMap.put("res_msg", "로그인한 사용자를 찾을 수 없습니다.");
            return resultMap;
        }

        if (dto.getMember_no() == null) {
            resultMap.put("res_msg", "회원 번호를 찾을 수 없습니다.");
            return resultMap;
        }

        int result = service.createNoticeApi(dto, files);

        if (result > 0) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "게시글 생성 성공");
        }

        return resultMap;
    }
    
    // 게시글 상세 화면
    @CheckPermission("NOTICE_R")
    @GetMapping("/notice/detail")
    public String detailView(@RequestParam("noticeNo") Long noticeNo, Model model) {    	
    	Notice notice = service.getNoticeDetail(noticeNo);
        if (notice == null) {
            // 게시글이 없는 경우 처리 (예: 목록으로 리다이렉트)
            return "redirect:/notice";
        }
        
        List<Attach> attachList= attachRepository.findByNotice(notice);
        model.addAttribute("attachList",attachList);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }
    
    // 게시글 수정 화면
    @CheckPermission("NOTICE_CRU")
    @PostMapping("/notice/update")
    @ResponseBody
    public Map<String, String> updateNoticeApi(@ModelAttribute NoticeDto dto,
    										   @RequestParam(value = "files", required = false) List<MultipartFile> files,
    										   @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFiles) {
    	// 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if (authentication == null || !authentication.isAuthenticated()) {
    		throw new AccessDeniedException("로그인 정보가 없습니다.");
    	}
    	
    	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
    	Long currentMemberNo = memberDetails.getMember().getMemberNo();
    	
    	if (!dto.getMember_no().equals(currentMemberNo)) {
    		throw new AccessDeniedException("접근 권한이 없습니다.");
    	}
    	
    	Map<String, String> result = new HashMap<>();
    	result.put("res_code", "500");
    	result.put("res_msg", "수정 실패");
    	
		/*
		 * String content = dto.getNotice_content() .replaceAll(",<p>","" )
		 * .replaceAll("</p>", "") .replaceAll("<p>", "") .trim();
		 * dto.setNotice_content(content);
		 */
    	
    	try {
    		int updateResult = service.updateNotice(dto, files, deleteFiles);
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
    @CheckPermission("NOTICE_CRU")
    @GetMapping("/notice/delete")
    public String deleteNotice(@RequestParam("noticeNo") Long noticeNo, @RequestParam("memberNo") Long memberNo, RedirectAttributes msg) {
    	// 본인이 아닌데 URL 을 바꿔서 진입하려고 하면 Security에 의해 차단해야 함
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if (authentication == null || !authentication.isAuthenticated()) {
    		throw new AccessDeniedException("로그인 정보가 없습니다.");
    	}
    	
    	MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
    	Long currentMemberNo = memberDetails.getMember().getMemberNo();
    	
    	if (!memberNo.equals(currentMemberNo)) {
    		throw new AccessDeniedException("접근 권한이 없습니다.");
    	}
    	
    	service.deleteNotice(noticeNo);
    	msg.addFlashAttribute("message","삭제가 완료되었습니다!");
    	return "redirect:/notice";
	}
    
}