package com.mjc.groupware.accommodationReservation.dto;

import com.mjc.groupware.accommodationReservation.entity.AccommodationInfo;

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
public class AccommodationInfoDto {
	
	private Long accommodation_no;
	private String accommodation_name;
	private String accommodation_type;
	private String accommodation_location;
	private String accommodation_content;
	private String accommodation_contact;
	private String accommodation_address;
	private Long room_count;
	private Long room_price;
	
	public AccommodationInfo toEntity() {
		return AccommodationInfo.builder()
				.accommodationNo(accommodation_no)
				.accommodationName(accommodation_name)
				.accommodationType(accommodation_type)
				.accommodationLocation(accommodation_location)
				.accommodationContent(accommodation_content)
				.accommodationContact(accommodation_contact)
				.accommodationAddress(accommodation_address)
				.roomCount(room_count)
				.roomPrice(room_price)
				.build();
	}
	
	public AccommodationInfoDto toDto(AccommodationInfo accommodationInfo) {
		return AccommodationInfoDto.builder()
				.accommodation_no(accommodationInfo.getAccommodationNo())
				.accommodation_name(accommodationInfo.getAccommodationName())
				.accommodation_type(accommodationInfo.getAccommodationType())
				.accommodation_location(accommodationInfo.getAccommodationLocation())
				.accommodation_content(accommodationInfo.getAccommodationContent())
				.accommodation_contact(accommodationInfo.getAccommodationContact())
				.accommodation_address(accommodationInfo.getAccommodationAddress())
				.room_count(accommodationInfo.getRoomCount())
				.room_price(accommodationInfo.getRoomPrice())
				.build();
	}
	
	
	
	
	
}
