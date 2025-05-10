package com.mjc.groupware.approval.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mjc.groupware.approval.dto.ApprovalAlarmDto;
import com.mjc.groupware.approval.entity.Approval;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalAlarmService {
	private final SimpMessagingTemplate messagingTemplate;
	
	public void sendAlarmToMembers(List<Long> memberNos, Approval approval, String message) {
        ApprovalAlarmDto dto = ApprovalAlarmDto.builder()
                .approvalNo(approval.getApprNo())
                .title("전자결재 도착")
                .message(message)
                .senderName(approval.getMember().getMemberName())
                .build();

        for (Long memberNo : memberNos) {
            messagingTemplate.convertAndSend("/topic/approval/alarm/" + memberNo, dto);
        }
    }
}
