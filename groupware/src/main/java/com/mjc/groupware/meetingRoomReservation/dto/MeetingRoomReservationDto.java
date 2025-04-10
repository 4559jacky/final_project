package com.mjc.groupware.meetingRoomReservation.dto;

import java.time.LocalDate;
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
public class MeetingRoomReservationDto {
	
	private int reservation_no;
	private int meeting_room_no;
	private String meeting_title;
	private LocalDate meeting_date;
	private LocalTime meeting_start_time;
	
}
