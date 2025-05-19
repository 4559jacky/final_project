package com.mjc.groupware.accommodationReservation.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.accommodationReservation.dto.AccommodationReservationAlarmDto;
import com.mjc.groupware.accommodationReservation.entity.AccommodationReservation;
import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.entity.AlarmMapping;
import com.mjc.groupware.common.websocket.repository.AlarmMappingRepository;
import com.mjc.groupware.common.websocket.repository.AlarmRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccommodationAlarmService {
	private final SimpMessagingTemplate messagingTemplate;
	private final AlarmRepository alarmRepository;
	private final MemberRepository memberRepository;
	private final AlarmMappingRepository alarmMappingRepository;
	
	@Transactional(rollbackFor=Exception.class)
	public void sendAlarmToAdmins(List<Long> admins, AccommodationReservation accommodationReservation, String message) {
		Alarm alarm = Alarm.builder()
				.alarmTitle("제휴숙소 신청")
				.alarmMessage(message)
				.accommodation(accommodationReservation.getAccommodationInfo())
				.build();
		
		Alarm saved = alarmRepository.save(alarm);
		
		AccommodationReservationAlarmDto dto = AccommodationReservationAlarmDto.builder()
				.alarmNo(saved.getAlarmNo())
				.title("제휴숙소 신청")
				.message(message)
				.alarmType("accommodationReservation")
				.otherPkNo(accommodationReservation.getAccommodationInfo().getAccommodationNo())
				.build();
		
		for (Long memberNo : admins) {
        	
            messagingTemplate.convertAndSend("/topic/accommodationReservation/alarm/" + memberNo, dto);
            
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
	
	@Transactional(rollbackFor=Exception.class)
	public void sendAlarmToMember(Long memberNo, AccommodationReservation accommodationReservation, String message) {
		Alarm alarm = Alarm.builder()
				.alarmTitle("제휴숙소 신청 결과")
				.alarmMessage(message)
				.accommodation(accommodationReservation.getAccommodationInfo())
				.build();
		
		Alarm saved = alarmRepository.save(alarm);
		
		AccommodationReservationAlarmDto dto = AccommodationReservationAlarmDto.builder()
				.alarmNo(saved.getAlarmNo())
				.title("제휴숙소 신청 결과")
				.message(message)
				.alarmType("reservationMember")
				.otherPkNo(memberNo)
				.build();
		
        	
        messagingTemplate.convertAndSend("/topic/accommodationReservation/alarm/" + memberNo, dto);
        
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
