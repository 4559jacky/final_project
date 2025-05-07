package com.mjc.groupware.attendance.dto;

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
public class SearchDto {
	private String year;
	private String month;
	private int order_type;
    private String checkInStatus = "";
    private String checkOutStatus = "";
}
