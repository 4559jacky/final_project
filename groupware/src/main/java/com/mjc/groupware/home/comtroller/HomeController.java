package com.mjc.groupware.home.comtroller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mjc.groupware.attendance.dto.AttendanceDto;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.attendance.repository.AttendanceRepository;
import com.mjc.groupware.attendance.repository.WorkSchedulePolicyRepository;
import com.mjc.groupware.attendance.service.AttendanceService;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;
import com.mjc.groupware.plan.entity.Plan;
import com.mjc.groupware.plan.service.PlanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final MemberService memberService;
	private final AttendanceService attendanceService;
	private final AttendanceRepository attendanceRepository;
	private final PlanService planService;
	private final WorkSchedulePolicyRepository workSchedulePolicyRepository;
	
	@GetMapping({"", "/", "/home"})
	public String homeView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		
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
	    
	    // 오늘 날짜의 휴가가 있는지
	    Plan plan = planService.selectAnnualPlan(member, today);
	    System.out.println("휴가 일정 : " + plan);
	    model.addAttribute("plan", plan);
	    
	    WorkSchedulePolicy wsp = workSchedulePolicyRepository.findById(1L).orElse(null);
	    model.addAttribute("workPolicy", wsp);
	    
		return "home";
	}
	
	@GetMapping("/starter")
	public String starterView() {
		return "starter";
	}
	
	@GetMapping("/sample")
	public String sampleView() {
		return "sample";
	}

}
