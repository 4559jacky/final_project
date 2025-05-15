package com.mjc.groupware.vote.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.board.entity.Board;
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

    /**
     * 투표 마감 시 알림 전송 및 저장
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendAlarmVoteMembers(List<Long> memberNos, Vote vote, String message) {
        Board board = vote.getBoard();

        // ✅ 중복 마감 알림 방지: 동일 게시글에 이미 마감 알림이 있는지 확인
        boolean exists = alarmRepository.findByBoardAndAlarmTitle(board, "투표 마감 알림").isPresent();
        if (exists) {
            System.out.println("⚠️ 이미 마감 알림이 존재합니다.");
            return;
        }

        // ✅ Alarm 저장
        Alarm alarm = Alarm.builder()
                .alarmTitle("투표 마감 알림")
                .alarmMessage(message)
                .board(board)
                .build();
        Alarm savedAlarm = alarmRepository.save(alarm);

        // ✅ 알림 전송 및 매핑 저장
        for (Long memberNo : memberNos) {
            Member member = memberRepository.findById(memberNo).orElse(null);
            if (member == null) continue;

            // 실명 여부에 따른 발신자 표시
            String senderName = "익명";
            if (!"Y".equalsIgnoreCase(vote.getIsAnonymous())) {
                String dept = member.getDept() != null ? member.getDept().getDeptName() : "부서없음";
                senderName = "[" + dept + "]" + member.getMemberName();
            }

            // WebSocket 알림 전송
            VoteAlarmDto dto = VoteAlarmDto.builder()
                    .voteNo(vote.getVoteNo())
                    .title("투표 마감 알림")
                    .boardNo(vote.getBoard().getBoardNo())  // ✅ 추가
                    .message(message)
                    .senderName(senderName)
                    .build();
            messagingTemplate.convertAndSend("/topic/vote/alarm/" + memberNo, dto);

            // 알림 매핑 저장
            AlarmMapping mapping = AlarmMapping.builder()
                    .alarm(savedAlarm)
                    .member(member)
                    .readYn("N")
                    .build();
            alarmMappingRepository.save(mapping);
        }
    }
}