package com.mjc.groupware.accommodationReservation.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjc.groupware.accommodationReservation.entity.AccommodationAttach;
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
	@JsonProperty("accommodationType")
	private String accommodation_type;
	@JsonProperty("accommodationName")
	private String accommodation_name;
	@JsonProperty("accommodationAddress")
	private String accommodation_address;
	@JsonProperty("accommodationPhone")
	private String accommodation_phone;
	@JsonProperty("accommodationEmail")
	private String accommodation_email;
	@JsonProperty("accommodationSite")
	private String accommodation_site;
	@JsonProperty("roomCount")
	private Long room_count;
	@JsonProperty("roomPrice")
	private Long room_price;
	@JsonProperty("accommodationLocation")
	private String accommodation_location;
	@JsonProperty("accommodationContent")
	private String accommodation_content;
	@JsonProperty("regDate")
	private LocalDateTime reg_date;
	@JsonProperty("modDate")
	private LocalDateTime mod_date;
//	private String accommodation_type;
//	private String accommodation_name;
//	private String accommodation_address;
//	private String accommodation_phone;
//	private String accommodation_email;
//	private String accommodation_site;
//	private Long room_count;
//	private Long room_price;
//	private String accommodation_location;
//	private String accommodation_content;
//	private LocalDateTime reg_date;
//	private LocalDateTime mod_date;
	
	private List<AccommodationAttach> attachList;
	private List<MultipartFile> files;
	
	public AccommodationInfo toEntity() {
		return AccommodationInfo.builder()
				.accommodationNo(this.accommodation_no != null && this.accommodation_no > 0 ? this.accommodation_no : null)
				.accommodationType(accommodation_type)
				.accommodationName(accommodation_name)
				.accommodationAddress(accommodation_address)
				.accommodationPhone(accommodation_phone)
				.accommodationEmail(accommodation_email)
				.accommodationSite(accommodation_site)
				.roomCount(room_count)
				.roomPrice(room_price)
				.accommodationLocation(accommodation_location)
				.accommodationContent(accommodation_content)
				.regDate(reg_date)
				.modDate(mod_date)
				.build();
	}
	
	public AccommodationInfoDto toDto(AccommodationInfo accommodationInfo) {
		return AccommodationInfoDto.builder()
				.accommodation_no(accommodationInfo.getAccommodationNo())
				.accommodation_type(accommodationInfo.getAccommodationType())
				.accommodation_name(accommodationInfo.getAccommodationName())
				.accommodation_address(accommodationInfo.getAccommodationAddress())
				.accommodation_phone(accommodationInfo.getAccommodationPhone())
				.accommodation_email(accommodationInfo.getAccommodationEmail())
				.accommodation_site(accommodationInfo.getAccommodationSite())
				.room_count(accommodationInfo.getRoomCount())
				.room_price(accommodationInfo.getRoomPrice())
				.accommodation_location(accommodationInfo.getAccommodationLocation())
				.accommodation_content(accommodationInfo.getAccommodationContent())
				.reg_date(accommodationInfo.getRegDate())
				.mod_date(accommodationInfo.getModDate())
				.build();
	}
	
	// ✅ 명시적 Getter (Thymeleaf 접근용)
//    public Long getAccommodationNo() {
//        return accommodation_no;
//    }
//
//    public String getAccommodationType() {
//        return accommodation_type;
//    }
//
//    public String getAccommodationName() {
//        return accommodation_name;
//    }
//
//    public String getAccommodationAddress() {
//        return accommodation_address;
//    }
//
//    public String getAccommodationPhone() {
//        return accommodation_phone;
//    }
//
//    public String getAccommodationEmail() {
//        return accommodation_email;
//    }
//
//    public String getAccommodationSite() {
//        return accommodation_site;
//    }
//
//    public Long getRoomCount() {
//        return room_count;
//    }
//
//    public Long getRoomPrice() {
//        return room_price;
//    }
//
//    public String getAccommodationLocation() {
//        return accommodation_location;
//    }
//
//    public String getAccommodationContent() {
//        return accommodation_content;
//    }
//
//    public LocalDateTime getRegDate() {
//        return reg_date;
//    }
//
//    public LocalDateTime getModDate() {
//        return mod_date;
//    }
	
	
	
}
