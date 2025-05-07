package com.mjc.groupware.attendance.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.attendance.entity.Attendance;

import jakarta.persistence.criteria.Join;

public class AttendanceSpecification {
	
	public static Specification<Attendance> attendanceMemberContains(Long keyword) {
		return (root, query, criteriaBuilder) -> {
	        Join<Object, Object> memberJoin = root.join("member");
	        return criteriaBuilder.equal(memberJoin.get("memberNo"), keyword);
	    };
	}
	
	public static Specification<Attendance> attendanceLateYnContains(String keyword) {
		return (root, query, criteriaBuilder) ->
		criteriaBuilder.like(root.get("lateYn"), "%"+keyword+"%");
	}
	
	public static Specification<Attendance> attendanceEarlyLeaveYnContains(String keyword) {
		return (root, query, criteriaBuilder) ->
		criteriaBuilder.like(root.get("earlyLeaveYn"), "%"+keyword+"%");
	}
	
	
	public static Specification<Attendance> attendDateAfter(LocalDate startDate) {
	    return (root, query, cb) -> {
	        if (startDate != null) {
	            return cb.greaterThanOrEqualTo(root.get("attendDate"), startDate);
	        } else {
	            return cb.conjunction(); // 조건 없음
	        }
	    };
	}
	
	public static Specification<Attendance> attendDateBefore(LocalDate endDate) {
	    return (root, query, cb) -> {
	        if (endDate != null) {
	            return cb.lessThanOrEqualTo(root.get("attendDate"), endDate);
	        } else {
	            return cb.conjunction(); // 조건 없음
	        }
	    };
	}
	

}
