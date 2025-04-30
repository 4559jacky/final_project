package com.mjc.groupware.accommodationReservation.entity;

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
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="accommodation_info")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AccommodationInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accommodation_no")
	private Long accommodationNo;
	
	@Column(name = "accommodation_name")
	private String accommodationName;
	
	@Column(name = "accommodation_type")
	private String accommodationType;
	
	@Column(name = "accommodation_location")
	private String accommodationLocation;
	
	@Column(name = "accommodation_content")
	private String accommodationContent;
	
	@Column(name = "accommodation_contact")
	private String accommodationContact;
	
	@Column(name = "accommodation_address")
	private String accommodationAddress;
	
	@Column(name = "room_count")
	private Long roomCount;
	
	@Column(name = "room_price")
	private Long roomPrice;
}
