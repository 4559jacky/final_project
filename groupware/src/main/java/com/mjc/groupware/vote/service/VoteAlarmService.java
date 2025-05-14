package com.mjc.groupware.vote.service;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;
import com.mjc.groupware.vote.dto.VoteAlarmDto;
import com.mjc.groupware.vote.entity.Vote;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoteAlarmService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MemberRepository memberRepository;

    public void sendAlarmToAllMembers(Vote vote, String message) {
        List<Long> allMemberNos = memberRepository.findAll()
                .stream()
                .map(Member::getMemberNo)
                .toList();

        sendAlarmVoteMembers(allMemberNos, vote, message); // 참여자 대신 전체 전송
    }

    public void sendAlarmVoteMembers(List<Long> memberNos, Vote vote, String message) {
        boolean isAnonymous = "Y".equalsIgnoreCase(vote.getIsAnonymous());

        for (Long memberNo : memberNos) {
            String senderName;

            if (isAnonymous) {
                senderName = "익명";
            } else {
                Member member = memberRepository.findById(memberNo).orElse(null);
                if (member != null) {
                    String deptName = member.getDept() != null ? member.getDept().getDeptName() : "부서없음";
                    String memberName = member.getMemberName() != null ? member.getMemberName() : "익명";
                    senderName = "[" + deptName + "]" + memberName;
                } else {
                    senderName = "알 수 없음";
                }
            }

            VoteAlarmDto dto = VoteAlarmDto.builder()
                .voteNo(vote.getVoteNo())
                .title("투표 마감 알림")
                .message(message)
                .senderName(senderName)
                .build();

            messagingTemplate.convertAndSend("/topic/vote/alarm/" + memberNo, dto);
        }
    }
}