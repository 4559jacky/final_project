package com.mjc.groupware.attendance.dto;

import java.time.LocalDateTime;
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
public class AttendanceDto {
	private Long attendance_no;
	private LocalDateTime check_in_time;
	private LocalDateTime check_out_time;
	private LocalTime working_time;
	private String attendance_status;
	private Long member_no;
}
