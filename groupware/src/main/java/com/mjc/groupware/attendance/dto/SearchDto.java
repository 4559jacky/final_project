package com.mjc.groupware.attendance.dto;

import java.time.LocalDate;

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
	private LocalDate start_date;
	private LocalDate end_date;
	private int order_type;
    private String check_in_status = "";
    private String check_out_status = "";
}
