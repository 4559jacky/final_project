package com.mjc.groupware.common.websocket.dto;

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
public class AlarmDto {
	private Long alarmNo;
	private String title;
	private String message;
	private String alarmType;
	private Long otherPkNo;
	private Long approvalMemberNo;
}
