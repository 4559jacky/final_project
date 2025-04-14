package com.mjc.groupware.meetingRoomReservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="meeting_room")
@Builder
public class MeetingRoom {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="meeting_room_no")
	private Long meetingRoomNo;
	
	@Column(name="meeting_room_name")
	private String meetingRoomName;
	
	@Column(name="meeting_room_status")
	private String meetingRoomStatus;
}
