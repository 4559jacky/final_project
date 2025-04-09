package com.mjc.groupware.meetingRoomReservation.dto;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class MeetingRoomDto {
	
	private int meeting_room_no;
	private String meeting_room_name;
	private String meeting_room_status;
	private String new_meeting_room_name;
	
	public MeetingRoom toEntity() {
		return MeetingRoom.builder()
						.meetingRoomNo(meeting_room_no)
						.meetingRoomName(meeting_room_name)
						.meetingRoomStatus(meeting_room_status)
						.build();
	}
	
	public MeetingRoomDto toDto(MeetingRoom meetingRoom) {
		return MeetingRoomDto.builder()
						.meeting_room_no(meetingRoom.getMeetingRoomNo())
						.meeting_room_name(meetingRoom.getMeetingRoomName())
						.meeting_room_status(meetingRoom.getMeetingRoomStatus())
						.build();
	}
}
