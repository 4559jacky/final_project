package com.mjc.groupware.attendance.specification;

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
	
	// 해당 달의 데이터를 가져옴
//	public static Specification<Attendance> attendanceMonthContains(String keyword) {
//		return (root, query, criteriaBuilder) ->
//		criteriaBuilder.like(root.get("attendDate"), "%"+keyword+"%");
//	}
	
	public static Specification<Attendance> attendanceLateYnContains(String keyword) {
		return (root, query, criteriaBuilder) ->
		criteriaBuilder.like(root.get("lateYn"), "%"+keyword+"%");
	}
	
	public static Specification<Attendance> attendanceEarlyLeaveYnContains(String keyword) {
		return (root, query, criteriaBuilder) ->
		criteriaBuilder.like(root.get("earlyLeaveYn"), "%"+keyword+"%");
	}
	
	

}
