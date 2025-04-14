package com.mjc.groupware.meetingRoomReservation.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;

public interface MeetingRoomReservationRepository extends JpaRepository<MeetingRoomReservation,Long>{
	List<MeetingRoomReservation> findAll();
	
	
}