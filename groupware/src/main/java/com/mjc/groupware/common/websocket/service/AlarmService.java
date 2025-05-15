package com.mjc.groupware.common.websocket.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.common.websocket.entity.Alarm;
import com.mjc.groupware.common.websocket.entity.AlarmMapping;
import com.mjc.groupware.common.websocket.repository.AlarmMappingRepository;
import com.mjc.groupware.common.websocket.repository.AlarmRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.vote.entity.Vote;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {
	
	private final AlarmRepository alarmRepository;
	private final AlarmMappingRepository alarmMappingRepository;
	private final MemberRepository memberRepository;
	
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
	@Transactional(rollbackFor = Exception.class)
	public void sendVoteAlarmToMembers(List<Long> memberNos, Vote vote) {
	    // ✅ 중복 체크: 이미 같은 게시글에 대해 마감 알림이 저장된 경우 return
	    boolean exists = alarmRepository
	        .findByBoardAndAlarmTitle(vote.getBoard(), "투표 마감 알림")
	        .isPresent();
	    if (exists) {
	        System.out.println("🔁 이미 마감 알림이 존재하여 중복 저장 방지");
	        return;
	    }

	    // 🔍 메시지 구성
	    String message;
	    if ("Y".equalsIgnoreCase(vote.getIsAnonymous())) {
	        message = "익명 투표가 마감되었습니다.";
	    } else {
	        String senderName = vote.getBoard().getMember().getMemberName();
	        String deptName = vote.getBoard().getMember().getDept() != null
	                ? vote.getBoard().getMember().getDept().getDeptName()
	                : "부서없음";
	        message = "[" + deptName + "] " + senderName + "님의 투표가 마감되었습니다.";
	    }

	    // 🧱 Alarm 생성 및 저장
	    Alarm alarm = Alarm.builder()
	        .alarmTitle("투표 마감 알림")
	        .alarmMessage(message)
	        .board(vote.getBoard())
	        .build();
	    Alarm savedAlarm = alarmRepository.save(alarm);

	    // 📩 각 사용자에게 알림 매핑 저장
	    for (Long memberNo : memberNos) {
	        Member member = memberRepository.findById(memberNo).orElse(null);
	        if (member == null) continue;

	        AlarmMapping mapping = AlarmMapping.builder()
	            .alarm(savedAlarm)
	            .member(member)
	            .readYn("N")
	            .build();

	        alarmMappingRepository.save(mapping);
	    }
	}
}