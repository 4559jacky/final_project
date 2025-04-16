package com.mjc.groupware.meetingRoomReservation.repository;


import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;

public interface MeetingRoomReservationRepository extends JpaRepository<MeetingRoomReservation,Long>
															,JpaSpecificationExecutor<MeetingRoomReservation>{
	List<MeetingRoomReservation> findAll(Specification<MeetingRoomReservation> spec);
	
	
	
}