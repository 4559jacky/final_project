package com.mjc.groupware.accommodationReservation.dto;

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
public class AccommodationReservationAlarmDto {
	private Long alarmNo;
    private String title;
    private String message;
    private String alarmType;
    private Long otherPkNo;
}
