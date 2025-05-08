package com.mjc.groupware.attendance.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.attendance.dto.AnnualLeavePolicyDto;
import com.mjc.groupware.attendance.dto.AttendPageDto;
import com.mjc.groupware.attendance.dto.AttendanceDto;
import com.mjc.groupware.attendance.dto.MemberAttendanceDto;
import com.mjc.groupware.attendance.dto.SearchDto;
import com.mjc.groupware.attendance.dto.WeeklyWorkDto;
import com.mjc.groupware.attendance.dto.WorkSchedulePolicyDto;
import com.mjc.groupware.attendance.entity.AnnualLeavePolicy;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.attendance.repository.AnnualLeavePolicyRepository;
import com.mjc.groupware.attendance.repository.AttendanceRepository;
import com.mjc.groupware.attendance.repository.WorkSchedulePolicyRepository;
import com.mjc.groupware.attendance.service.AttendanceService;
import com.mjc.groupware.dept.entity.Dept;
import com.mjc.groupware.dept.service.DeptService;
import com.mjc.groupware.member.dto.MemberDto;
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
	private final MemberService memberService;
	private final AttendanceRepository attendanceRepository;
	
	@GetMapping("/attendance/management")
	public String attendanceManagementViewApi(Model model, MemberSearchDto searchDto, PageDto pageDto) {

	    // 기본값: 오늘 날짜
	    LocalDate targetDate = (searchDto.getTarget_date() != null) ? searchDto.getTarget_date() : LocalDate.now();

	    // 페이징 처리
	    if (pageDto.getNowPage() == 0) pageDto.setNowPage(1);
	    Page<Member> memberPage = service.selectMemberAll(searchDto, pageDto);
	    pageDto.setTotalPage(memberPage.getTotalPages());

	    // 날짜별 출근 정보 바인딩
	    List<MemberAttendanceDto> dtoList = new ArrayList<>();
	    for (Member member : memberPage.getContent()) {
	        Attendance att = attendanceRepository.findByMemberAndAttendDate(member, targetDate);
	        dtoList.add(new MemberAttendanceDto(member, att));
	    }

	    model.addAttribute("targetDate", targetDate); // 👉 뷰에서 날짜 초기값 표시용
	    model.addAttribute("memberAttendanceList", dtoList);
	    model.addAttribute("pageDto", pageDto);
	    model.addAttribute("searchDto", searchDto);
	    
	    WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
	    model.addAttribute("workSchedulePolicy", wsp);
	    
	    // 부서/직책/정책 리스트
	    model.addAttribute("deptList", deptService.selectDeptAll());
	    model.addAttribute("posList", posService.selectPosAll());
	    model.addAttribute("annualLeavePolicyList", annualLeavePolicyRepository.findAllByOrderByYearAsc());

	    return "/attendance/admin/attendanceManagement";
	}
	
	// 근태 정책 업데이트
	@PostMapping("/attendance/manage")
	@ResponseBody
	public Map<String,Object> workTimeUpdateApi(WorkSchedulePolicyDto dto) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		resultMap = attendanceService.workTimeUpdateApi(dto);
		
		return resultMap;
	}
	
	// 연차 관리페이지로 이동
	@GetMapping("/annual/management")
	public String annualManagementViewApi(Model model, MemberSearchDto searchDto, PageDto pageDto) {
		
		if(pageDto.getNowPage() == 0) pageDto.setNowPage(1);
		
		Page<Member> memberList = service.selectMemberAllForAnnual(searchDto, pageDto);
		
		pageDto.setTotalPage(memberList.getTotalPages());
		
		List<Dept> deptList = deptService.selectDeptAll();
		List<Pos> posList = posService.selectPosAll();
		List<AnnualLeavePolicy> annualLeavePolicyList = annualLeavePolicyRepository.findAllByOrderByYearAsc();
		
		
		
		model.addAttribute("memberList", memberList);
		model.addAttribute("deptList", deptList);
		model.addAttribute("posList", posList);
		model.addAttribute("searchDto", searchDto);
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
	public List<AnnualLeavePolicy> annualPolicyUpdateApi(AnnualLeavePolicyDto dto) {
		
		int result = attendanceService.annualPolicyUpdateApi(dto);
		if(result > 0) {
			return annualLeavePolicyRepository.findAllByOrderByYearAsc();
		}
		
		return new ArrayList<>();
	}
	
	// 연차 정책 삭제
	@DeleteMapping("/annualPolicy/delete/{year}")
	@ResponseBody
	public List<AnnualLeavePolicy> annualPolicyDeleteApi(@PathVariable("year") int year) {
		
		int result = attendanceService.annualPolicyDeleteApi(year);
		
		if(result > 0) {
			return annualLeavePolicyRepository.findAllByOrderByYearAsc();
		}
		
		return new ArrayList<>();
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
	public String attendanceInfoViewApi(Model model, @AuthenticationPrincipal UserDetails userDetails, AttendPageDto pageDto, SearchDto searchDto) {
		
		String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member member = memberService.selectMemberOne(memberDto);

	    LocalDate today = LocalDate.now();

	    Attendance attendance = attendanceRepository.findByMember_MemberNoAndAttendDate(member.getMemberNo(), today);
	    if (attendance != null) {
	        AttendanceDto dto = new AttendanceDto().toDto(attendance);
	        model.addAttribute("todayAttendance", dto);
	    }
	    model.addAttribute("member", member);
	    
	    WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
	    model.addAttribute("workPolicy", wsp);
	    
	    List<Attendance> attendanceList = attendanceService.selectAttendanceAll(member);
	    model.addAttribute("pageDto", pageDto);
	    model.addAttribute("searchDto", searchDto);
	    
	    return "/attendance/user/attendanceInfo";
	}
	
	@GetMapping("/attendance/log")
	public String attendanceLogViewApi(Model model, @AuthenticationPrincipal UserDetails userDetails, AttendPageDto pageDto, SearchDto searchDto) {
		
		String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member member = memberService.selectMemberOne(memberDto);
	    
	    if(pageDto.getNowPage() == 0) pageDto.setNowPage(1);
	    
	    Page<Attendance> attendancePageList = attendanceService.selectAttendanceAllByFilter(member, searchDto, pageDto);
	    pageDto.setTotalPage(attendancePageList.getTotalPages());
	    
	    model.addAttribute("attendanceList", attendancePageList);
	    model.addAttribute("pageDto", pageDto);
	    model.addAttribute("searchDto", searchDto);
	    model.addAttribute("member", member);
		
		return "/attendance/user/attendanceLog";
	}
	
	// 출근 시간 저장
	@PostMapping("/attendance/saveStartTime")
	@ResponseBody
	public Map<String,Object> saveStartTime(@RequestBody AttendanceDto dto,
	                            @AuthenticationPrincipal UserDetails userDetails) {

	    // 유저 정보
	    String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member entity = memberService.selectMemberOne(memberDto);
	    MemberDto member = new MemberDto().toDto(entity);
	    
	    Map<String,Object> resultMap = attendanceService.saveStartTime(member, dto);
	    
	    return resultMap;
	}
	
	// 퇴근 시간 저장
	@PostMapping("/attendance/saveEndTime")
	@ResponseBody
	public Map<String,Object> saveEndTime(@RequestBody AttendanceDto dto,
								@AuthenticationPrincipal UserDetails userDetails) {
		// 유저 정보
	    String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member entity = memberService.selectMemberOne(memberDto);
	    MemberDto member = new MemberDto().toDto(entity);
	    
	    Map<String,Object> resultMap = attendanceService.saveEndTime(member, dto);
	    
	    return resultMap;
	}
	
	
	// 오늘 기준 이번주의 근무시간 가져오기
	@GetMapping("/attendance/weeklyHours")
	@ResponseBody
	public List<WeeklyWorkDto> getWeeklyWorkTime( @RequestParam(name = "startDate") String startDate,
		    @RequestParam(name = "endDate") String endDate, @AuthenticationPrincipal UserDetails userDetails) {
		// 유저 정보
	    String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member entity = memberService.selectMemberOne(memberDto);
	    MemberDto member = new MemberDto().toDto(entity);
	    
	    return attendanceService.getWeeklyWorkTime(startDate, endDate, member);
	}
}
