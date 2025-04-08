package com.mjc.groupware.meetingRoomReservation.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mjc.groupware.meetingRoomReservation.dto.MeetingRoomDto;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;
import com.mjc.groupware.meetingRoomReservation.repository.MeetingRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetingRoomService {

	private final MeetingRoomRepository repository;
	
	// 관리자 - 회의실 목록 전체 조회
	public List<MeetingRoom> selectMeetingRoomAll(){
		return repository.findAll();
	}
	
	// 관리자 - 회의실 등룍
	public int adminCreateMeetingRoom(MeetingRoomDto dto) {
		int result = 0;
		
		try {
			MeetingRoom entity = dto.toEntity();
			repository.save(entity);
			result = 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	// 관리자 - 회의실 삭제
	public int adminDeleteMeetingRoom(int id) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(id).orElse(null);
			
			if(target != null) {
				MeetingRoomDto dto = new MeetingRoomDto().toDto(target);
				dto.setMeeting_room_status("D");
				repository.save(dto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 관리자 - 회의실 상태 변경
	public int adminUpdateMeetingRoomStatus(int id) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(id).orElse(null);
			
			if(target != null) {
				MeetingRoomDto dto = new MeetingRoomDto().toDto(target);
				if(dto.getMeeting_room_status().equals("Y")) {
					dto.setMeeting_room_status("N");
				}else if(dto.getMeeting_room_status().equals("N")) {
					dto.setMeeting_room_status("Y");
				}
				repository.save(dto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}

	// 관리자 - 회의실 수정
	public int adminUpdateMeetingRoom(MeetingRoomDto dto) {
		int result = 0;
		try {
			MeetingRoom target = repository.findById(dto.getMeeting_room_no()).orElse(null);
			
			if(target != null) {
				MeetingRoomDto meetingRoomDto = new MeetingRoomDto().toDto(target);
				meetingRoomDto.setMeeting_room_name(dto.getNew_meeting_room_name());
				repository.save(meetingRoomDto.toEntity());
				result = 1;
			}			
		}catch(Exception e ) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	////////////////////////////////////////////////
	
}
