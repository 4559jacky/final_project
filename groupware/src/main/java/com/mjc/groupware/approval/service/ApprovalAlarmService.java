package com.mjc.groupware.approval.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.approval.dto.ApprovalAlarmDto;
import com.mjc.groupware.approval.entity.Approval;
import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.entity.AlarmMapping;
import com.mjc.groupware.common.websocket.repository.AlarmMappingRepository;
import com.mjc.groupware.common.websocket.repository.AlarmRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalAlarmService {
	private final SimpMessagingTemplate messagingTemplate;
	private final AlarmRepository alarmRepository;
	private final MemberRepository memberRepository;
	private final AlarmMappingRepository alarmMappingRepository;
	
	@Transactional(rollbackFor=Exception.class)
	public void sendAlarmToMembers(List<Long> memberNos, Approval approval, String message) {
		
		Alarm alarm = Alarm.builder()
        		.alarmTitle("전자결재")
        		.alarmMessage(message)
        		.approval(approval)
        		.build();
        
        Alarm saved = alarmRepository.save(alarm);
		
		ApprovalAlarmDto dto = ApprovalAlarmDto.builder()
				.alarmNo(saved.getAlarmNo())
                .title("전자결재")
                .message(message)
                .senderName(approval.getMember().getMemberName())
                .alarmType("approval")
                .otherPkNo(approval.getApprNo())
                .approvalMemberNo(approval.getMember().getMemberNo())
                .build();
        

        for (Long memberNo : memberNos) {
        	
            messagingTemplate.convertAndSend("/topic/approval/alarm/" + memberNo, dto);
            
            Member member = memberRepository.findById(memberNo).orElse(null);
            
            if(member != null) {
            	
            	AlarmMapping alarmMapping = AlarmMapping.builder()
                		.alarm(saved)
                		.member(member)
                		.readYn("N")
                		.build();
            	
            	alarmMappingRepository.save(alarmMapping);
            }

        }
    }
}
