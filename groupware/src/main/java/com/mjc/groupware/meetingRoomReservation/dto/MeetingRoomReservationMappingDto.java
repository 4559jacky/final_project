package com.mjc.groupware.meetingRoomReservation.dto;

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
public class MeetingRoomReservationMappingDto {
	
	private int mapping_no;
	private int reservation_no;
	private int member_no;
	
}
