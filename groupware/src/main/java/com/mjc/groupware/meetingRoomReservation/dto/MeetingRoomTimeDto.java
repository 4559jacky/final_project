package com.mjc.groupware.meetingRoomReservation.dto;

import java.time.LocalTime;

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
public class MeetingRoomTimeDto {

	private int meeting_time_no;
	private LocalTime meeting_start_time;
	
}
