package com.mjc.groupware.dept.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.dept.dto.DeptDto;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DeptController {
	
	private Logger logger = LoggerFactory.getLogger(DeptController.class);
	
	private final DeptService service;
	private final MemberService memberService;
	
	@GetMapping("/admin/dept/create")
	public String createDeptView(Model model) {
		List<Dept> deptList = service.selectDeptAll();
		List<Member> memberList = memberService.selectMemberAll();
		
		model.addAttribute("deptList", deptList);
		model.addAttribute("memberList", memberList);
		
		return "dept/create";
	}
	
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
	
	@GetMapping("/admin/dept/select")
	@ResponseBody
	public Map<String, String> selectDeptByDeptNoApi(@RequestParam("deptId") String deptId) {
		
		Map<String, String> resultMap = new HashMap<>();
		
		resultMap.put("res_code", "500");
        resultMap.put("res_msg", "부서 조회 중 알 수 없는 오류가 발생했습니다.");
        
        logger.info("deptId: {}", deptId);

		try {
			if (deptId != null && !deptId.trim().isEmpty()) {
			    Long deptNo = Long.parseLong(deptId);
			    
			    Dept result = service.selectDeptByDeptNo(deptNo);
			    
			    resultMap.put("dept_name", result.getDeptName());
			    resultMap.put("dept_location", result.getDeptLocation());
			    resultMap.put("dept_phone", result.getDeptPhone());
			    
			    if(result.getMember() == null) {
			    	resultMap.put("member_no", "0");
			    } else {
			    	resultMap.put("member_no", result.getMember().getMemberNo().toString());
			    }
			    
			    if(result.getParentDept() == null) {
			    	resultMap.put("parent_dept_no", "0");
			    } else {
			    	resultMap.put("parent_dept_no", result.getParentDept().getDeptNo().toString());
			    }
			    
			    if(result != null) {
					resultMap.put("res_code", "200");
					resultMap.put("res_msg", "부서가 성공적으로 조회되었습니다.");
			    }
			}
		} catch(IllegalArgumentException e) {
			resultMap.put("res_code", "400");
	        resultMap.put("res_msg", e.getMessage());
		} catch(Exception e) {
			logger.error("부서 수정 중 오류 발생", e);
			resultMap.put("res_code", "500");
	        resultMap.put("res_msg", "부서 조회 중 알 수 없는 오류가 발생했습니다.");
		}

		return resultMap;
	}
	
}
