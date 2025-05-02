package com.mjc.groupware.attendance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceController {
	
	// 근태 관리페이지로 이동
	@GetMapping("/attendance/management")
	public String attendanceManagementViewApi() {
		return "/attendance/admin/attendanceManagement";
	}
	
	// 근태 페이지로 이동
	@GetMapping("/attendance/info")
	public String attendanceInfoViewApi() {
	    return "/attendance/user/attendanceInfo"; // templates/AttendanceInfo.html 렌더링
	}
	
	
	
}
