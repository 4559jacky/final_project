package com.mjc.groupware.accommodationReservation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mjc.groupware.accommodationReservation.entity.AccommodationReservation;
import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;
import com.mjc.groupware.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AccommodationReservationDto {
	
	private Long reservation_no;
	private Long number_of_people;
	private LocalDateTime reservation_date;
	private LocalDate check_in;
	private LocalDate check_out;
	private String reservation_status;
	private Long member_no;
	private String member_name;
	private Long accommodation_no;
	private String accommodation_name;
	private Long room_price;
	private Long room_count;
	private String reject_reason;
	
	public AccommodationReservation toEntity() {
		return AccommodationReservation.builder()
				.reservationNo(reservation_no)
				.numberOfPeople(number_of_people)
				.reservationDate(reservation_date)
				.checkIn(check_in)
				.checkOut(check_out)
				.reservationStatus(reservation_status)
				.member(Member.builder().memberNo(member_no).build())
				.accommodationInfo(AccommodationInfo.builder().accommodationNo(accommodation_no).build())
				.roomCount(room_count)
				.rejectReason(reject_reason)
				.build();
	}
	
	public AccommodationReservationDto toDto(AccommodationReservation accommodation) {
		return AccommodationReservationDto.builder()
				.reservation_no(accommodation.getReservationNo())
				.number_of_people(accommodation.getNumberOfPeople())
				.reservation_date(accommodation.getReservationDate())
				.check_in(accommodation.getCheckIn())
				.check_out(accommodation.getCheckOut())
				.reservation_status(accommodation.getReservationStatus())
				.member_no(accommodation.getMember() != null ? accommodation.getMember().getMemberNo() : null)
				.member_name(accommodation.getMember() != null ? accommodation.getMember().getMemberName() : null)
				.accommodation_no(accommodation.getAccommodationInfo() != null ? accommodation.getAccommodationInfo().getAccommodationNo() : null)
				.accommodation_name(accommodation.getAccommodationInfo() != null ? accommodation.getAccommodationInfo().getAccommodationName() : null)
				.room_price(accommodation.getAccommodationInfo() != null ? accommodation.getAccommodationInfo().getRoomPrice() : null)
				.room_count(accommodation.getRoomCount())
				.reject_reason(accommodation.getRejectReason())
				.build();
	}
	
	
}
