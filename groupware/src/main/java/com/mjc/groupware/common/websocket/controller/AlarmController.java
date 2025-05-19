package com.mjc.groupware.common.websocket.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mjc.groupware.common.websocket.dto.AlarmDto;
import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.service.AlarmService;
import com.mjc.groupware.member.dto.MemberDto;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AlarmController {
	
	private final MemberService memberService;
	private final AlarmService alarmService;
	
	// 알림 읽음처리
	@GetMapping("/alarm/read/{id}")
	@ResponseBody
	public void updateAlarmReadStatus(@PathVariable("id") Long alarmNo, @AuthenticationPrincipal UserDetails userDetails) {
		System.out.println("알람 읽음 상태로 변경");
		
		String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member member = memberService.selectMemberOne(memberDto);
	    
	    alarmService.updateAlarmReadStatus(alarmNo, member.getMemberNo());
	}
	
	@PostMapping("/alarm/list")
	@ResponseBody
	public List<AlarmDto> selectAlarmAllApi(@AuthenticationPrincipal UserDetails userDetails){
		String userId = userDetails.getUsername();
	    MemberDto memberDto = new MemberDto();
	    memberDto.setMember_id(userId);
	    Member member = memberService.selectMemberOne(memberDto);
	    
	    List<Alarm> alarmList = alarmService.selectAlarmAllApi(member.getMemberNo());
	    
	    List<AlarmDto> dtos = new ArrayList<>();
	    for(Alarm alarm : alarmList) {
	    	AlarmDto dto = AlarmDto.builder()
	    			.alarmNo(alarm.getAlarmNo())
	    			.title(alarm.getAlarmTitle())
	    			.message(alarm.getAlarmMessage())
	    			.build();
	    	if(alarm.getApproval() != null) {
	    		dto.setAlarmType("approval");
	    		dto.setOtherPkNo(alarm.getApproval().getApprNo());
	    		dto.setApprovalMemberNo(alarm.getApproval().getMember().getMemberNo());
	    	} else if(alarm.getNotice() != null) {
	    		dto.setAlarmType("notice");
	    		dto.setOtherPkNo(alarm.getNotice().getNoticeNo());
	    	} else if(alarm.getBoard() != null) {
	    		dto.setAlarmType("board");
	    		dto.setOtherPkNo(alarm.getBoard().getBoardNo());
	    	} else if(alarm.getAccommodation() != null && "제휴숙소 신청".equals(alarm.getAlarmTitle())) {
	    		dto.setAlarmType("accommodationReservation");
	    		dto.setOtherPkNo(alarm.getAccommodation().getAccommodationNo());
	    	} else if(alarm.getAccommodation() != null && "제휴숙소 신청 결과".equals(alarm.getAlarmTitle())) {
	    		dto.setAlarmType("reservationMember");
	    		dto.setOtherPkNo(alarm.getAccommodation().getAccommodationNo());
	    	}
	    	dtos.add(dto);
	    }
	    
	    return dtos;
	}

}
