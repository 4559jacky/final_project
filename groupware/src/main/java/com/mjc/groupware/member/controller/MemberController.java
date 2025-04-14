package com.mjc.groupware.member.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberAttachDto;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberAttachService;
import com.mjc.groupware.member.service.MemberService;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.repository.PosRepository;
import com.mjc.groupware.pos.service.PosService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	private static int cnt = 0;
	
	private final MemberService service;
	private final MemberAttachService memberAttachService;
	private final PosService posService;
	private final DeptService deptService;
	private final PosRepository posRepository;
	
	@GetMapping("/login")
	public String loginView(
			@RequestParam(value="error", required=false) String error,
			@RequestParam(value="errorMsg", required=false) String errorMsg,
			Model model) {
		
		if(error == null) {
			cnt = 0;
		} else {
			cnt++;
		}
		
		model.addAttribute("error", error);
		model.addAttribute("errorMsg", errorMsg);
		
		System.out.println("MemberController - 로그인 연속 실패 횟수 : "+cnt+"회");
		
		return "member/login";
	}
	
	@GetMapping("/admin/member/create")
	public String createMemberView(Model model) {
		List<Pos> posList = posService.selectPosAllByPosOrderAsc();
		List<Dept> deptList = deptService.selectDeptAll();
		
		model.addAttribute("posList", posList);
		model.addAttribute("deptList", deptList);
		
		Optional<Pos> maxOrderPos = posRepository.findTopByOrderByPosOrderDesc();
		maxOrderPos.ifPresent(pos -> model.addAttribute("selectedPosNo", pos.getPosNo()));
		
		return "member/create";
	}
	
	@PostMapping("/admin/member/create")
	@ResponseBody
	public Map<String, String> createMemberApi(MemberDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "사원 등록 도중 알 수 없는 오류가 발생하였습니다.");
		
		try {
			Member member = service.createMember(dto);
			
			if(member != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "사원 등록이 성공적으로 완료되었습니다.");
			}
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("사원 등록 중 오류 발생", e);
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "사원 등록 도중 알 수 없는 오류가 발생하였습니다.");
		} 
		
		return resultMap;
	}
	
	@GetMapping("/member/{id}/update")
	public String updateMyProfileView(@PathVariable("id") Long memberNo, Model model) {
		// 각각의 사원이 본인의 정보를 수정하는 페이지로 이동
		Member member = service.selectMemberOneByMemberNo(MemberDto.builder().member_no(memberNo).build());
		
		model.addAttribute("member", member);
		
		return "member/myPage";
	}
	
	@PostMapping("/member/{id}/update/image")
	@ResponseBody
	public Map<String, String> updateMyProfile(@PathVariable("id") Long memberNo, MemberAttachDto memberAttachDto) {
		// 각각의 사원이 본인의 프로필 이미지를 수정하는 로직
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "프로필 정보 수정 중 알 수 없는 오류가 발생하였습니다.");
		
		if(memberAttachDto != null) {
			try {
				MultipartFile file = memberAttachDto.getProfile_image();
				
				MemberAttachDto param = memberAttachService.uploadFile(file);
				param.setMember_no(memberNo);
				
				memberAttachService.updateMyProfile(param);
				
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "프로필 사진 수정이 성공적으로 완료되었습니다.");
			} catch(Exception e) {
				logger.error("프로필 사진 수정 중 오류 발생", e);
				resultMap.put("res_code", "500");
		        resultMap.put("res_msg", "프로필 사진 수정 중 알 수 없는 오류가 발생했습니다.");
			}
		}
		
		return resultMap;
		
	}
	
	@PostMapping("/member/{id}/update/pw")
	@ResponseBody
	public Map<String, String> updatemMemberPw(@PathVariable("id") Long memberNo, MemberDto dto, HttpServletResponse response) {
		Map<String, String> resultMap = new HashMap<>();

		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "비밀번호 수정 중 알 수 없는 오류가 발생하였습니다.");
		
		try {
			dto.setMember_no(memberNo);
			
			service.updateMemberPw(dto);
			
			Cookie rememberMeCookie = new Cookie("remember-me", null);
			rememberMeCookie.setMaxAge(0);
			rememberMeCookie.setPath("/");
			response.addCookie(rememberMeCookie);

			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "비밀번호 수정이 성공적으로 완료되었습니다.");
		} catch(IllegalArgumentException e) {
			logger.warn("비밀번호 수정 실패 - 잘못된 입력: {}", e.getMessage());
			resultMap.put("res_code", "400");
			resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("비밀번호 수정 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "비밀번호 수정 중 알 수 없는 오류가 발생하였습니다.");
		}
		
		return resultMap;
		
	}

	 // 결재라인 부서의 속한 사원들 select
	 @GetMapping("/member/dept/{id}")
	 @ResponseBody
	 public List<MemberDto> selectMemberAllByDeptId(@PathVariable("id") Long id) {
		 List<Member> memberList = service.selectMemberAllByDeptId(id);
		 List<MemberDto> memberDtoList = new ArrayList<MemberDto>();
		 for(Member m : memberList) {
			 MemberDto dto = new MemberDto().toDto(m);
			 memberDtoList.add(dto);
		 }
		 
		 return memberDtoList; 
	 }
	 
	 @GetMapping("/member/{id}")
	 @ResponseBody
	 public MemberDto selectMemberOneById(@PathVariable("id") Long id) {
		 MemberDto dto = new MemberDto();
		 dto.setMember_no(id);
		 Member member = service.selectMemberOneByMemberNo(dto);
		 MemberDto memberDto = new MemberDto().toDto(member);
		 return memberDto;
	 }
	
}
