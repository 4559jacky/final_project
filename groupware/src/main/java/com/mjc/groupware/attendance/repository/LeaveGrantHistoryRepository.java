package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.attendance.dto.LeaveGrantHistory;

public interface LeaveGrantHistoryRepository extends JpaRepository<LeaveGrantHistory, Long> {

	@Query("SELECT COUNT(l) > 0 FROM LeaveGrantHistory l WHERE l.member.memberNo = :memberNo AND l.startDate = :start AND l.endDate = :end")
	boolean existsByMemberNoAndPeriod(
	    @Param("memberNo") Long memberNo,
	    @Param("start") LocalDate start,
	    @Param("end") LocalDate end
	);
}