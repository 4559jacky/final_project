package com.mjc.groupware.notice.dto;

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
public class NoticeAlarmDto {
	private Long alarmNo;
    private String title;
    private String message;
    private String senderName;
    private String alarmType;
    private Long otherPkNo;
}
