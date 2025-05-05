package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Attendance findByMember_MemberNoAndAttendDate(Long memberNo, LocalDate attendDate);
}
