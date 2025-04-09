package com.mjc.groupware.meetingRoomReservation.entity;

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
@Table(name="meeting_time_mapping")
@Builder
public class MeetingTimeMapping {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mapping_no")
	private int mappingNo;
	
	@ManyToOne
	@JoinColumn(name="meeting_time_no")
	private MeetingTime meetingTimeNo;
	
	@ManyToOne
	@JoinColumn(name="reservation_no")
	private MeetingRoomReservation reservationNo;
	
}
