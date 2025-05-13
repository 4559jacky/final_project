package com.mjc.groupware.attendance.dto;

import java.time.LocalDate;

import com.mjc.groupware.attendance.entity.Holiday;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class HolidayDto {
	private Long id;
	private String holiday_name;
	private LocalDate holiday_date;
	
	public Holiday toEntity() {
		return Holiday.builder()
				.id(this.id)
				.name(this.holiday_name)
				.date(this.holiday_date)
				.build();
	}
	
	public HolidayDto toDto(Holiday holiday) {
		return HolidayDto.builder()
				.id(holiday.getId())
				.holiday_name(holiday.getName())
				.holiday_date(holiday.getDate())
				.build();
	}
}
