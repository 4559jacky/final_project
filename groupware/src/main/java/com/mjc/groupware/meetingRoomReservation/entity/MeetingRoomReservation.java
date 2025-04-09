package com.mjc.groupware.meetingRoomReservation.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="meeting_room_reservation")
@Builder
public class MeetingRoomReservation {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="reservation_no")
	private int reservationNo;
	
	@ManyToOne
	@JoinColumn(name="meeting_room_no")
	private MeetingRoom meetingRoomNo;
	
	@Column(name="meeting_title")
	private String meetingTitle;
	
	@Column(name="meeting_date")
	private LocalDate meetingDate;
	
	
}
