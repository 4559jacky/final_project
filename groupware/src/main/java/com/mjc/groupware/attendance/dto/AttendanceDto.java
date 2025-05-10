package com.mjc.groupware.attendance.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.member.entity.Member;

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
	private LocalDate attend_date;
	private LocalTime check_in;
	private LocalTime check_out;
	private LocalTime working_time;
	private String late_yn;
	private String early_leave_yn;
	private Long member_no;
	private String plan_title;
	
	public Attendance toEntity () {
		return Attendance.builder()
				.attendanceNo(this.attendance_no)
				.attendDate(this.attend_date)
				.checkIn(this.check_in)
				.checkOut(this.check_out)
				.workingTime(this.working_time)
				.lateYn(this.late_yn)
				.earlyLeaveYn(this.early_leave_yn)
				.member(Member.builder().memberNo(this.member_no).build())
				.build();
	}
	
	public AttendanceDto toDto(Attendance entity) {
		return AttendanceDto.builder()
				.attendance_no(entity.getAttendanceNo())
				.attend_date(entity.getAttendDate())
				.check_in(entity.getCheckIn())
				.check_out(entity.getCheckOut())
				.working_time(entity.getWorkingTime())
				.late_yn(entity.getLateYn())
				.early_leave_yn(entity.getEarlyLeaveYn())
				.member_no(entity.getMember().memberNo)
				.build();
	}
}
