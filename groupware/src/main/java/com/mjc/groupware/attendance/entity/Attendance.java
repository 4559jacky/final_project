package com.mjc.groupware.attendance.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "attendance")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="attendance_no")
	private Long attendanceNo;
	
	@Column(name="check_in_time")
	private LocalDateTime checkInTime;
	
	@Column(name="check_out_time")
	private LocalDateTime checkOutTime;
	
	@Column(name="working_time")
	private LocalTime workingTime;
	
	@Column(name="attendance_status")
	private String attendanceStatus;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member member;
	
}
