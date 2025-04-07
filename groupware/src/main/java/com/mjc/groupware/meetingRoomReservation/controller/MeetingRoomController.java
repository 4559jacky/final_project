package com.mjc.groupware.meetingRoomReservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MeetingRoomController {
	
	// 사용자 회의실 예약 페이지 전환
	@GetMapping("/meetingRoom")
	public String meetingRoomView() {
		return "/meetingRoom/meetingRoomReservation";
	}
	
	//////////////////////////////////////////////////////////////////
	
	
	
	// 관리자 회의실 관리 페이지 전환
	@GetMapping("/admin/meetingRoom")
	public String adminMeetingRoomView() {
		return "/meetingRoom/AdminMeetingRoomReservation";
	}
}
