package com.mjc.groupware.notice.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.entity.AlarmMapping;
import com.mjc.groupware.common.websocket.repository.AlarmMappingRepository;
import com.mjc.groupware.common.websocket.repository.AlarmRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.notice.dto.NoticeAlarmDto;
import com.mjc.groupware.notice.entity.Notice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeAlarmService {
	private final SimpMessagingTemplate messagingTemplate;
	private final AlarmRepository alarmRepository;
	private final MemberRepository memberRepository;
	private final AlarmMappingRepository alarmMappingRepository;
	
	@Transactional(rollbackFor=Exception.class)
	public void sendAlarmToAllMembers(List<Long> memberAll, Notice notice, String message) {
		Alarm alarm = Alarm.builder()
				.alarmTitle("긴급공지")
				.alarmMessage(message)
				.notice(notice)
				.build();
		
		Alarm saved = alarmRepository.save(alarm);
		
		NoticeAlarmDto dto = NoticeAlarmDto.builder()
				.alarmNo(saved.getAlarmNo())
				.title("긴급공지")
				.message(message)
				.alarmType("notice")
				.otherPkNo(notice.getNoticeNo())
				.build();
		
		for (Long memberNo : memberAll) {
        	
            messagingTemplate.convertAndSend("/topic/notice/alarm/" + memberNo, dto);
            
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
