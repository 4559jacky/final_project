package com.mjc.groupware.meetingRoomReservation.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.meetingRoomReservation.Service.MeetingRoomService;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomDto;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomReservationDto;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingRoomController {
	
	private final MeetingRoomService service;
	
	
	// 사용자 - 회의실 예약 페이지 전환
	@GetMapping("/meetingRoom")
	public String meetingRoomView(Model model) {
		MeetingRoomReservationDto dto = new MeetingRoomReservationDto();
		List<MeetingRoomReservationDto> resultList = service.selectMeetingRoomReservationAll(dto);
		
		List<MeetingRoom> meetingRoomList = service.adminSelectMeetingRoomAll();
		
		model.addAttribute("reservationList",resultList);
		model.addAttribute("meetingRoomList",meetingRoomList);
		
		return "/meetingRoom/meetingRoomReservation";
	}
	
	// 사용자 - 전체 회의실 예약 일정 캘린더에 조회
	@GetMapping("/selectReservation")
	@ResponseBody
	public List<Map<String, Object>> selectMeetingRoomReservationAll() {
		MeetingRoomReservationDto filterDto = new MeetingRoomReservationDto();
	    List<MeetingRoomReservationDto> reservations = service.selectMeetingRoomReservationAll(filterDto);
	    List<Map<String, Object>> events = new ArrayList<>();

	    for (MeetingRoomReservationDto dto : reservations) {
	        events.addAll(dto.toFullCalendarEvents());
	    }

	    return events;
	}


	// 사용자 - 회의실, 시간 조회
	@PostMapping("/selectMeetingRoom")
	@ResponseBody
	public List<MeetingRoom> selectMeetingRoomAll() {
	    return service.selectMeetingRoomAll();
	}
	
	
	
	// 사용자 - 회의실 예약
	@PostMapping("/reservation")
	@ResponseBody
	public Map<String,String> createMeetingRoomReservation(MeetingRoomReservationDto dto) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회의실 예약 중 오류가 발생하였습니다.");
		
		int result = service.createMeetingRoomReservation(dto);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회의실 예약이 완료되었습니다.");
		}
		
		return resultMap;
	}
	
	
	//////////////////////////////////////////////////////////////////
	
	// 관리자 - 회의실 전체 예약 내역 조회
	@GetMapping("/admin/meetingReservation")
	public String adminSelectMeetingReservationAll(Model model) {
		MeetingRoomReservationDto dto = new MeetingRoomReservationDto();
		List<MeetingRoomReservationDto> resultList = service.selectMeetingRoomReservationAll(dto);
		
		List<MeetingRoom> meetingRoomList = service.adminSelectMeetingRoomAll();
		
		model.addAttribute("reservationList",resultList);
		model.addAttribute("meetingRoomList",meetingRoomList);
		 
		return "/meetingRoom/AdminMeetingReservation";
	}
	
	// 관리자 - 회의실 전체 예약 내역 조회 필터
	@PostMapping("/admin/selectFilter")
	@ResponseBody
	public List<MeetingRoomReservationDto> adminSelectFilter(@RequestBody Map<String,String> param){
		MeetingRoomReservationDto dto = new MeetingRoomReservationDto();
		
		String roomNo = param.get("roomNo");
		String date = param.get("date");
		
		if(roomNo != null && !roomNo.isEmpty()) {
			dto.setMeeting_room_no(Long.parseLong(roomNo));
		}
		if(date != null && !date.isEmpty()) {
			dto.setMeeting_date(LocalDate.parse(date));		
		}

		
		List<MeetingRoomReservationDto> list = service.selectMeetingRoomReservationAll(dto);
		
		return list;
	}
	
	// 관리자 - 전체 회의실 목록 조회
	@GetMapping("/admin/meetingRoom")
	public String adminMeetingRoomView(Model model) {
		
		List<MeetingRoom> resultList = service.adminSelectMeetingRoomAll();
		
		model.addAttribute("meetingRoomList",resultList);

		return "/meetingRoom/AdminMeetingRoomSelect";
	}
	
	// 관리자 -  회의실 등록
	@PostMapping("/admin/create")
	@ResponseBody
	public Map<String,String> adminCreateMeetingRoom(MeetingRoomDto dto) {
		
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회의실 등록 중 오류가 발생하였습니다.");
		
		int result = service.adminCreateMeetingRoom(dto);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회의실 등록이 완료되었습니다.");
		}
		
		return resultMap;
	}
	
	// 관리자 - 회의실 삭제
	@DeleteMapping("/admin/delete/{id}")
	@ResponseBody
	public Map<String,String> adminDeleteMeetingRoom(@PathVariable("id") Long id){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회의실 삭제 중 오류가 발생하였습니다.");
		
		int result = service.adminDeleteMeetingRoom(id);
		
		if(result > 0 ) {
			resultMap.put("res_code","200");
			resultMap.put("res_msg", "회의실 삭제가 완료되었습니다.");
		}
		return resultMap;
	}
	
	// 관리자 - 회의실 상태 변경 
	@PostMapping("/admin/updateStatus/{id}")
	@ResponseBody
	public Map<String,String> adminUpdateMeetingRoomStatus(@PathVariable("id") Long id){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회의실 상태 변경 중 오류가 발생하였습니다.");
		
		int result = service.adminUpdateMeetingRoomStatus(id);
		
		if(result > 0 ) {
			resultMap.put("res_code","200");
			resultMap.put("res_msg", "회의실 상태 변경이 완료되었습니다.");
		}
		return resultMap;
	}
	
	// 관리자 - 회의실 수정
		@PostMapping("/admin/update")
		@ResponseBody
		public Map<String,String> adminUpdateMeetingRoom(MeetingRoomDto dto){
			Map<String,String> resultMap = new HashMap<String,String>();
			resultMap.put("res_code", "500");
			resultMap.put("res_msg", "회의실 수정 중 오류가 발생하였습니다.");
			
			int result = service.adminUpdateMeetingRoom(dto);
			
			if(result > 0 ) {
				resultMap.put("res_code","200");
				resultMap.put("res_msg", "회의실 수정이 완료되었습니다.");
			}
			return resultMap;
		}
	
}
