package com.mjc.groupware.meetingRoomReservation.entity;

import com.mjc.groupware.member.entity.Member;

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
@Table(name="reservation_mapping")
@Builder
public class MeetingRoomReservationMapping {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mapping_no")
	private int mappingNo;
	
	@ManyToOne
	@JoinColumn(name="reservation_no")
	private MeetingRoomReservation reservationNo;
	
	@ManyToOne
	@JoinColumn(name="member_no")
	private Member memberNo;
	
}
