package com.mjc.groupware.meetingRoomReservation.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.meetingRoomReservation.Service.MeetingRoomService;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomDto;
import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomReservationDto;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MeetingRoomController {
	
	private final MeetingRoomService service;
	
	
	// 사용자 - 회의실 예약 페이지 전환
	@CheckPermission("MEETING_USER")
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
	@CheckPermission("MEETING_USER")
	@GetMapping("/selectReservation")
	@ResponseBody
	public List<Map<String, Object>> selectMeetingRoomReservationAll(HttpServletRequest request) {
		// URL 직접 접근을 차단 :: Ajax 요청이 아니면 차단
		String header = request.getHeader("X-Custom-Ajax");
		if (!"true".equals(header)) {
		    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "허용되지 않은 접근입니다.");
		}
		
		MeetingRoomReservationDto filterDto = new MeetingRoomReservationDto();
	    List<MeetingRoomReservationDto> reservations = service.selectMeetingRoomReservationAll(filterDto);
	    List<Map<String, Object>> events = new ArrayList<>();

	    for (MeetingRoomReservationDto dto : reservations) {
	    	  if (!"N".equals(dto.getReservation_status())) {
	              events.addAll(dto.toFullCalendarEvents());
	          }
	    }

	    return events;
	}


	// 사용자 - 회의실, 시간 조회
	@CheckPermission("MEETING_USER")
	@PostMapping("/selectMeetingRoom")
	@ResponseBody
	public List<MeetingRoom> selectMeetingRoomAll() {
	    return service.selectMeetingRoomAll();
	}
	
	
	
	// 사용자 - 회의실 예약
	@CheckPermission("MEETING_USER")
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
	
	// 사용자 - 회의실 예약 정보 조회 
	@CheckPermission("MEETING_USER")
	@PostMapping("/reservationInfo")
	@ResponseBody
	public List<MeetingRoomReservationDto> selectReservationById(@RequestBody Map<String,String> param){
		MeetingRoomReservationDto dto = new MeetingRoomReservationDto();
		
		String roomNo = param.get("meetingRoomNo");
		String date = param.get("selectedDate");
		
		if(roomNo != null && !roomNo.isEmpty()) {
			dto.setMeeting_room_no(Long.parseLong(roomNo));
		}
		if(date != null && !date.isEmpty()) {
			dto.setMeeting_date(LocalDate.parse(date));		
		}
		
		List<MeetingRoomReservationDto> list = service.selectMeetingRoomReservationAll(dto);
		List<MeetingRoomReservationDto> result = new ArrayList<>();
		
		for (MeetingRoomReservationDto res : list) {
			// ✅ 날짜와 회의실 번호가 모두 일치할 때만 추가
		    if (res.getMeeting_room_no().equals(dto.getMeeting_room_no())
		    		&& res.getMeeting_date().equals(dto.getMeeting_date())
		    		&& res.getReservation_status().equals("Y")) {
		    	result.add(res);
		    }
		}
		
		return result;
	}
	
	// 사용자 - 회의실 예약 취소
	@CheckPermission("MEETING_USER")
	@PostMapping("/delete/{id}")
	@ResponseBody
	public Map<String,String> deleteReservation(@PathVariable("id") Long reservationNo) {
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "500");
		resultMap.put("res_msg", "회의실 예약 취소 중 오류가 발생하였습니다.");
		
		int result = service.deleteReservation(reservationNo);
		
		if(result>0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "회의실 취소가 완료되었습니다.");
		}
		
		return resultMap;
	}
	
	// 사용자 - 내 회의실 예약 내역 필터 
	@CheckPermission("MEETING_USER")
	@PostMapping("/selectFilter")
	@ResponseBody
	public List<MeetingRoomReservationDto> SelectFilter(@RequestBody Map<String,String> param){
		MeetingRoomReservationDto dto = new MeetingRoomReservationDto();
		
		String roomNo = param.get("roomNo");
		String date = param.get("date");
		String memberNo = param.get("memberNo");
		
		if(roomNo != null && !roomNo.isEmpty()) {
			dto.setMeeting_room_no(Long.parseLong(roomNo));
		}
		if(date != null && !date.isEmpty()) {
			dto.setMeeting_date(LocalDate.parse(date));		
		}

		List<MeetingRoomReservationDto> list = service.selectMeetingRoomReservationAll(dto);
		List<MeetingRoomReservationDto> result = new ArrayList<>();

		// ✅ for문 돌면서 member_no에 내가 있는지만 확인해서 추가
		if (memberNo != null && !memberNo.isEmpty()) {
			Long myMemberNo = Long.parseLong(memberNo);
			
		    for (MeetingRoomReservationDto res : list) {
		    	List<Long> memberList = res.getMember_no();
		    	if (memberList != null && memberList.contains(myMemberNo)) {
		        	result.add(res);
		        }
		    }
		    
		}
		
		return result;
	}
	
	//////////////////////////////////////////////////////////////////
	
	// 관리자 - 회의실 전체 예약 내역 조회
	@CheckPermission("MEETING_RESERVATION_ADMIN")
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
	@CheckPermission("MEETING_RESERVATION_ADMIN")
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
	@CheckPermission("MEETING_ADMIN")
	@GetMapping("/admin/meetingRoom")
	public String adminMeetingRoomView(Model model) {
		List<MeetingRoom> resultList = service.adminSelectMeetingRoomAll();
		
		model.addAttribute("meetingRoomList",resultList);

		return "/meetingRoom/AdminMeetingRoomSelect";
	}
	
	// 관리자 - 회의실 등록
	@CheckPermission("MEETING_ADMIN")
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
	@CheckPermission("MEETING_ADMIN")
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
	@CheckPermission("MEETING_ADMIN")
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
	@CheckPermission("MEETING_ADMIN")
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
