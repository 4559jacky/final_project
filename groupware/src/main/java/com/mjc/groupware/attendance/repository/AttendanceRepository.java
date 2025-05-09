package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.attendance.entity.WorkSchedulePolicy;
import com.mjc.groupware.member.entity.Member;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Attendance findByMember_MemberNoAndAttendDate(Long memberNo, LocalDate attendDate);
	List<Attendance> findByAttendDateBetweenAndMember(LocalDate startDate, LocalDate endDate, Member member);
	Page<Attendance> findAll(Specification<Attendance> spec, Pageable pageable);
	Attendance findByMemberAndAttendDate(Member member, LocalDate targetDate);
}
