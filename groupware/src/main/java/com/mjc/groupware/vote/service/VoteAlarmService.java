package com.mjc.groupware.vote.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.vote.dto.VoteAlarmDto;
import com.mjc.groupware.vote.entity.Vote;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteAlarmService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendAlarmVoteMembers(List<Long> memberNos, Vote vote, String message) {
        Member writer = vote.getBoard().getMember();
        String senderName = "[" + writer.getDept().getDeptName() + "]" + writer.getMemberName();

        VoteAlarmDto dto = VoteAlarmDto.builder()
            .voteNo(vote.getVoteNo())
            .title("투표 마감 알림")
            .message(message)
            .senderName(senderName) // ✅ 여기서 부서+이름 조합
            .build();

        for (Long memberNo : memberNos) {
            messagingTemplate.convertAndSend("/topic/vote/alarm/" + memberNo, dto);
        }
    }
}