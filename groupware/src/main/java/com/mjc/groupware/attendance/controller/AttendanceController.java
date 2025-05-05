package com.mjc.groupware.attendance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.attendance.dto.AnnualLeavePolicyDto;
import com.mjc.groupware.attendance.dto.WorkSchedulePolicyDto;
import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.attendance.repository.WorkSchedulePolicyRepository;
import com.mjc.groupware.attendance.service.AttendanceService;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberSearchDto;
import com.mjc.groupware.member.dto.PageDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;
import com.mjc.groupware.member.service.RoleService;
import com.mjc.groupware.pos.entity.Pos;
import com.mjc.groupware.pos.service.PosService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AttendanceController {
	
	private final MemberService service;
	private final PosService posService;
	private final DeptService deptService;
	private final RoleService roleService;
	private final AttendanceService attendanceService;
	private final WorkSchedulePolicyRepository workSchedulePolicyRepository;
	private final AnnualLeavePolicyRepository annualLeavePolicyRepository;
	
	// 근태 관리페이지로 이동
	@GetMapping("/attendance/management")
	public String attendanceManagementViewApi(Model model) {
		
		WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
		model.addAttribute("workSchedulePolicy", wsp);
		
		return "/attendance/admin/attendanceManagement";
	}
	
	// 근태 정책 업데이트
	@PostMapping("/attendance/manage")
	@ResponseBody
	public Map<String,String> workTimeUpdateApi(WorkSchedulePolicyDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "근태 정책 변경에 실패하였습니다.");
		
		int result = attendanceService.workTimeUpdateApi(dto);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "근태 정책이 변경되었습니다.");
		}
		
		return resultMap;
	}
	
	// 연차 관리페이지로 이동
	@GetMapping("/annual/management")
	public String annualManagementViewApi(Model model, MemberSearchDto searchDto, PageDto pageDto) {
		
		if(pageDto.getNowPage() == 0) pageDto.setNowPage(1);
		
		Page<Member> memberList = service.selectMemberAll(searchDto, pageDto);
		
		pageDto.setTotalPage(memberList.getTotalPages());
		
		List<Dept> deptList = deptService.selectDeptAll();
		List<Pos> posList = posService.selectPosAll();
		List<AnnualLeavePolicy> annualLeavePolicyList = annualLeavePolicyRepository.findAllByOrderByYearAsc();
		
		
		
		model.addAttribute("memberList", memberList);
		model.addAttribute("deptList", deptList);
		model.addAttribute("posList", posList);
		model.addAttribute("searchText", searchDto.getSearch_text());
		model.addAttribute("pageDto", pageDto);
		model.addAttribute("annualLeavePolicyList", annualLeavePolicyList);
		
		return "/attendance/admin/annualLeaveManagement";
	}
	
	
	@GetMapping("/annual")
	@ResponseBody
	public List<AnnualLeavePolicy> annualPolicyViewApi() {
		List<AnnualLeavePolicy> resultList = annualLeavePolicyRepository.findAllByOrderByYearAsc();
		return resultList;
	}
	
	// 연차 정책 업데이트
	@PostMapping("/annualPolicy/update")
	@ResponseBody
	public Map<String,String> annualPolicyUpdateApi(AnnualLeavePolicyDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "연차 정책 변경에 실패하였습니다.");
		
		int result = attendanceService.annualPolicyUpdateApi(dto);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "연차 정책이 변경되었습니다.");
		}
		
		return resultMap;
	}
	
	// 연차 정책 삭제
	@DeleteMapping("/annualPolicy/delete/{year}")
	@ResponseBody
	public Map<String,String> annualPolicyDeleteApi(@PathVariable("year") int year) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "연차 정책 삭제에 실패하였습니다.");
		
		int result = attendanceService.annualPolicyDeleteApi(year);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "연차 정책이 삭제되었습니다.");
		}
		
		return resultMap;
	}
	
	@PostMapping("/member/annual/update/{id}")
	@ResponseBody
	public Map<String,String> memberAnnualUpdateApi(@PathVariable("id") Long memberNo, @RequestParam("annual_leave") double annualLeave) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회원 연차 수정을 실패하였습니다.");
		
		int result = attendanceService.memberAnnualUpdateApi(memberNo, annualLeave);
		
		if(result > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회원 연차가 수정되었습니다.");
		}
		
		return resultMap;
	}
	
	
	
	
	
	// 일반 사용자 근태 정보
	
	
	// 근태 페이지로 이동
	@GetMapping("/attendance/info")
	public String attendanceInfoViewApi() {
	    return "/attendance/user/attendanceInfo"; // templates/AttendanceInfo.html 렌더링
	}
	
	
	
}
