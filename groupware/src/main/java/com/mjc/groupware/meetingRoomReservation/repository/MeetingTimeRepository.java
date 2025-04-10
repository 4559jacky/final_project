package com.mjc.groupware.meetingRoomReservation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.meetingRoomReservation.entity.MeetingTime;

public interface MeetingTimeRepository extends JpaRepository<MeetingTime, Integer> {
    
	List<MeetingTime> findAll();

}