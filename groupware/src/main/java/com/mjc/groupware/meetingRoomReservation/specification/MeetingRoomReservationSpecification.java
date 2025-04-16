package com.mjc.groupware.meetingRoomReservation.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;

public class MeetingRoomReservationSpecification {
	
	// Long이나 LocalDate는 equal
	
	// 회의실 번호가 일치하는 검색 조건
    public static Specification<MeetingRoomReservation> meetingReservationContainsMeeitngRoomNo(Long keyword){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("meetingRoomNo").get("meetingRoomNo"), keyword);
    }

    // 날짜가 일치하는 검색 조건
    public static Specification<MeetingRoomReservation> meetingReservationContainsMeeitngDate(LocalDate keyword){
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("meetingDate"), keyword);
    }
}
