package com.mjc.groupware.meetingRoomReservation.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingRoom;

public interface MeetingRoomRepository extends JpaRepository<MeetingRoom,Long>{

	List<MeetingRoom> findAll();
	
}
