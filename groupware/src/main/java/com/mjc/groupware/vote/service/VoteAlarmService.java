package com.mjc.groupware.vote.service;

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
import com.mjc.groupware.vote.dto.VoteAlarmDto;
import com.mjc.groupware.vote.entity.Vote;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteAlarmService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AlarmRepository alarmRepository;
    private final AlarmMappingRepository alarmMappingRepository;
    private final MemberRepository memberRepository;

    @Transactional(rollbackFor = Exception.class)
    public void sendAlarmVoteMembers(List<Long> memberNos, Vote vote, String message) {

        // 1. Alarm 엔티티 저장
        Alarm alarm = Alarm.builder()
            .alarmTitle("투표 마감 알림")
            .alarmMessage(message)
            .board(vote.getBoard()) // 📌 Board 연관관계 사용
            .build();

        Alarm saved = alarmRepository.save(alarm);

        // 2. WebSocket 전송 및 AlarmMapping 저장
        for (Long memberNo : memberNos) {

            Member member = memberRepository.findById(memberNo).orElse(null);
            if (member == null) continue;

            // 보낸 사람 이름 구성
            String senderName = "익명";
            if (!"Y".equalsIgnoreCase(vote.getIsAnonymous())) {
                String dept = member.getDept() != null ? member.getDept().getDeptName() : "부서없음";
                senderName = "[" + dept + "]" + member.getMemberName();
            }

            // 3. VoteAlarmDto 생성
            VoteAlarmDto dto = VoteAlarmDto.builder()
                .voteNo(vote.getVoteNo())
                .title("투표 마감 알림")
                .message(message)
                .senderName(senderName)
                .build();

            // 4. STOMP WebSocket 전송
            messagingTemplate.convertAndSend("/topic/vote/alarm/" + memberNo, dto);

            // 5. AlarmMapping 저장 (안 읽은 상태로)
            AlarmMapping alarmMapping = AlarmMapping.builder()
                .alarm(saved)
                .member(member)
                .readYn("N")
                .build();

            alarmMappingRepository.save(alarmMapping);
        }
    }
}