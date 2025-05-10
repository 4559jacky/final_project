package com.mjc.groupware.accommodationReservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="accommodation_reservation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AccommodationReservation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_no")
	private Long reservationNo;
	
	@Column(name = "number_of_people")
	private Long numberOfPeople;
	
	@Column(name = "reservation_date")
	private LocalDateTime reservationDate;
	
	@Column(name = "check_in")
	private LocalDate checkIn;
	
	@Column(name = "check_out")
	private LocalDate checkOut;
	
	@Column(name = "reservation_status")
	private String reservationStatus;
	
	// 사번
	@ManyToOne
	@JoinColumn(name = "member_no")
	private Member member;
	
	// 숙소번호
	@ManyToOne
	@JoinColumn(name = "accommodation_no")
	private AccommodationInfo accommodationInfo;
	
	@Column(name = "reject_reason")
	private String rejectReason;
	
}
