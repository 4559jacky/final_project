package com.mjc.groupware.meetingRoomReservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Long reservation_no;
	private Long meeting_room_no;
	private String meeting_title;
	private LocalDate meeting_date;
	private List<LocalTime> meeting_start_time;
	private List<Long> member_no;
	private List<String> member_name;
	private String meeting_room_name;
	private String reservation_status;
	
	public List<Map<String, Object>> toFullCalendarEvents() {
	    List<Map<String, Object>> events = new ArrayList<>();

	    for (LocalTime startTime : this.meeting_start_time) {
	        Map<String, Object> event = new HashMap<>();

	        LocalDateTime startDateTime = LocalDateTime.of(this.meeting_date, startTime);

	        event.put("title", this.meeting_title);
	        event.put("start", startDateTime.toString());
	        event.put("location", this.meeting_room_name); // ← 여기 추가!

	        Map<String, Object> extendedProps = new HashMap<>();
	        extendedProps.put("meetingRoomNo", this.meeting_room_no);
	        extendedProps.put("members", this.member_no);
	        extendedProps.put("roomName", this.meeting_room_name); // ← 이것도 넣어줘

	        event.put("extendedProps", extendedProps);

	        events.add(event);
	    }

	    return events;
	}

}
