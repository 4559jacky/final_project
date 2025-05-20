package com.mjc.groupware.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.attendance.entity.Attendance;
import com.mjc.groupware.member.entity.Member;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Attendance findByMember_MemberNoAndAttendDate(Long memberNo, LocalDate attendDate);
	List<Attendance> findByAttendDateBetweenAndMember(LocalDate startDate, LocalDate endDate, Member member);
	Page<Attendance> findAll(Specification<Attendance> spec, Pageable pageable);
	Attendance findByMemberAndAttendDate(Member member, LocalDate targetDate);
	
	// 출근 체크가 null이 아닌 경우만 (실제로 출근한 날만 필터링하려면 아래처럼 추가)
	@Query("SELECT COUNT(a) FROM Attendance a WHERE a.member.memberNo = :memberNo AND a.attendDate BETWEEN :start AND :end")
	long countWorkedDays(
	    @Param("memberNo") Long memberNo,
	    @Param("start") LocalDate start,
	    @Param("end") LocalDate end
	);
	
	@Query("SELECT a.attendDate FROM Attendance a WHERE a.member.memberNo = :memberNo AND a.attendDate BETWEEN :start AND :end")
    List<LocalDate> findAttendedDates(
        @Param("memberNo") Long memberNo,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
