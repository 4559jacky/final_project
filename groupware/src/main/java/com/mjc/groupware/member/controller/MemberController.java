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

import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;
import com.mjc.groupware.pos.controller.PosController;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.repository.PosRepository;
import com.mjc.groupware.pos.service.PosService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {
	
	private Logger logger = LoggerFactory.getLogger(PosController.class);
	
	private static int cnt = 0;
	
	private final MemberService service;
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
		
		logger.info("MemberDto : {}", dto);
		
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
	

	 // 결재라인 부서의 속한 사원들 select
	 @GetMapping("/member/dept/{id}")
	 @ResponseBody public List<MemberDto> selectMemberAllByDeptId(@PathVariable("id") Long id) {
		 List<Member> memberList = service.selectMemberAllByDeptId(id);
		 List<MemberDto> memberDtoList = new ArrayList<MemberDto>();
		 for(Member m : memberList) {
			 MemberDto dto = new MemberDto().toDto(m);
			 memberDtoList.add(dto);
		 }
		 
		 return memberDtoList; 
	 }
	 
	
}
