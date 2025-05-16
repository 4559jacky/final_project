package com.mjc.groupware.common.websocket.service;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.entity.AlarmMapping;
import com.mjc.groupware.common.websocket.repository.AlarmMappingRepository;
import com.mjc.groupware.common.websocket.repository.AlarmRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	
	private final AlarmRepository alarmRepository;
	private final AlarmMappingRepository alarmMappingRepository;
	
	@Transactional(rollbackFor=Exception.class)
	public void updateAlarmReadStatus(Long alarmNo, Long memberNo) {
		
		try {
			AlarmMapping alarmMapping = alarmMappingRepository
				    .findByAlarm_AlarmNoAndMember_MemberNo(alarmNo, memberNo)
				    .orElseThrow(() -> new IllegalArgumentException("해당 알림 매핑이 존재하지 않습니다."));
				
			alarmMapping.updateReadYn("Y");
			
			alarmMappingRepository.save(alarmMapping);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	@Transactional(rollbackFor=Exception.class)
	public List<Alarm> selectAlarmAllApi(Long memberNo) {
		List<Alarm> alarmList = new ArrayList<Alarm>();
		try {
			List<AlarmMapping> mappings = alarmMappingRepository.findByMember_MemberNoAndReadYnOrderByAlarm_RegDateDesc(memberNo, "N");
			
			for (AlarmMapping mapping : mappings) {
				
	            alarmList.add(mapping.getAlarm());
	        }
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return alarmList;
	}
	
}