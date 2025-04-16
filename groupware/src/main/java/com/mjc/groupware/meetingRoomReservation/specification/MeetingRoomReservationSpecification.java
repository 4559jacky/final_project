package com.mjc.groupware.meetingRoomReservation.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;

public class MeetingRoomReservationSpecification {
	
	// 회의실이 일치하는 검색 조건
	public static Specification<MeetingRoomReservation> meetingReservationContainsMeeitngRoomNo(Long keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("meetingRoomNo"),"%"+keyword+"%");
	}
	
	// 날짜가 일치하는 검색 조건
	public static Specification<MeetingRoomReservation> meetingReservationContainsMeeitngDate(LocalDate keyword){
		return (root, query, criteriaBuilder) ->
			criteriaBuilder.like(root.get("meetingDate"),"%"+keyword+"%");
	}
}
