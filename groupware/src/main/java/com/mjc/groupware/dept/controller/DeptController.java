package com.mjc.groupware.dept.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.dept.dto.DeptDto;
import com.mjc.groupware.dept.dto.DeptResponseDto;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeptController {
	
	private Logger logger = LoggerFactory.getLogger(DeptController.class);
	
	private final DeptService service;
	private final MemberService memberService;
	
	@CheckPermission("DEPT_ADMIN_CRU")
	@GetMapping("/admin/dept/create")
	public String createDeptView(Model model) {
		List<Dept> deptList = service.selectByDeptStatusNot(3);
		List<Member> memberList = memberService.selectMemberAll();
		
		List<Member> membersWithoutDept = new ArrayList<>();
	    for (Member member : memberList) {
	        if (member.getDept() == null) {
	            membersWithoutDept.add(member);
	        }
	    }
		
		model.addAttribute("deptList", deptList);
		model.addAttribute("memberList", memberList);
		model.addAttribute("memberWithoutDept", membersWithoutDept);
		
		return "dept/create";
	}
	
	@CheckPermission("DEPT_ADMIN_CRU")
	@PostMapping("/admin/dept/create")
	@ResponseBody
	public Map<String, String> createDeptApi(DeptDto dto) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
        resultMap.put("res_msg", "부서 등록 중 알 수 없는 오류가 발생했습니다.");
        
        logger.info("DeptDto: {}", dto);
        
		try {
			if (dto.getMember_no() != null && dto.getMember_no() == 0) {
			    dto.setMember_no(null);
			}
			
			if (dto.getParent_dept_no() != null && dto.getParent_dept_no() == 0) {
			    dto.setParent_dept_no(null);
			}
			
			Dept result = service.createDept(dto);
			
			if(result != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "부서가 성공적으로 등록되었습니다.");				
			}
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("부서 등록 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "부서 등록 중 알 수 없는 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
	@CheckPermission("DEPT_ADMIN_CRU")
	@GetMapping("/admin/dept/select")
	@ResponseBody
	public DeptResponseDto selectDeptByDeptNoApi(@RequestParam("deptId") String deptId) {
		DeptResponseDto resultDto = null;
		
        logger.info("deptId: {}", deptId);

		try {
			if (deptId != null && !deptId.trim().isEmpty()) {
			    Long deptNo = Long.parseLong(deptId);
			    
			    Dept result = service.selectDeptByDeptNo(deptNo);
			    
			    if (result != null) {
	                // 부서에 속한 직원 리스트 가져오기
	                List<MemberDto> members = new ArrayList<>();
	                if (result.getMembers() != null && !result.getMembers().isEmpty()) {
	                    for (Member member : result.getMembers()) {
	                        members.add(MemberDto.builder()
	                                .member_no(member.getMemberNo())
	                                .member_name(member.getMemberName())
	                                .build());
	                    }
	                }
	                
	                resultDto = new DeptResponseDto(
		                    result.getDeptNo(),
		                    result.getDeptName(),
		                    result.getDeptStatus(),
		                    result.getDeptLocation(),
		                    result.getDeptPhone(),
		                    result.getMember() != null ? result.getMember().getMemberNo() : 0L,
		                    result.getParentDept() != null ? result.getParentDept().getDeptNo() : 0L,
		                    members
		                    );
	                
	                return resultDto;
	            } else {
	                throw new IllegalArgumentException("부서를 찾을 수 없습니다.");
	            }
			}
		} catch(IllegalArgumentException e) {
			logger.error("잘못된 부서 ID", e);
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 부서 No입니다.");
		} catch(Exception e) {
			logger.error("부서 조회 중 오류 발생", e);
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "부서 조회 중 알 수 없는 오류 발생하였습니다.");
		}

		return null;
	}
	
	@CheckPermission("DEPT_ADMIN_CRU")
	@PostMapping("/admin/dept/update")
	@ResponseBody
	public Map<String, String> updateDept(DeptDto dto, @RequestParam(value="transferDeptNo", required=false) Long transferDeptNo) {
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
        resultMap.put("res_msg", "부서 정보 수정 중 알 수 없는 오류가 발생했습니다.");
        
        logger.info("DeptDto: {}", dto);
		
		try {
			if(0 == dto.getMember_no()) {
				dto.setMember_no(null);
			}
			
			if(0 == dto.getParent_dept_no()) {
				dto.setParent_dept_no(null);
			}
			
			Dept result = service.updateDept(dto);
			
			if(result != null) {
				if (dto.getDept_status() == 3) {
			        memberService.transferMembersOfDept(dto.getDept_no(), transferDeptNo);
			    }
				
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "부서 정보가 성공적으로 수정되었습니다.");
		    }
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("부서 수정 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "부서 정보 수정 중 알 수 없는 오류가 발생했습니다.");
		}
		
		return resultMap;
	}
	
}
