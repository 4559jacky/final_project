package com.mjc.groupware.approval.dto;

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
public class ApprovalAlarmDto {
	private Long alarmNo;
    private String title;
    private String message;
    private Long receiverNo;
    private Long senderNo;
    private String senderName;
    private String alarmType;
    private Long otherPkNo;
    private Long approvalMemberNo;
}
