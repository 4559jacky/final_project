package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.member.entity.Member;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Attendance findByMember_MemberNoAndAttendDate(Long memberNo, LocalDate attendDate);
	List<Attendance> findByAttendDateBetweenAndMember(LocalDate startDate, LocalDate endDate, Member member);
}
