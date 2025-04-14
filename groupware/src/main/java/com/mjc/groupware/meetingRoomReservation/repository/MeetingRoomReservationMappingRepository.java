package com.mjc.groupware.meetingRoomReservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservation;
import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoomReservationMapping;

public interface MeetingRoomReservationMappingRepository extends JpaRepository<MeetingRoomReservationMapping,Long>{
	List<MeetingRoomReservationMapping> findAll();
	
	List<MeetingRoomReservationMapping> findByReservationNo(MeetingRoomReservation reservationNo);
}
