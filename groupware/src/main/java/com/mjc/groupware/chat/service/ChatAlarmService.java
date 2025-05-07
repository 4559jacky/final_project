package com.mjc.groupware.chat.service;

import org.springframework.stereotype.Service;

import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.repository.ChatAlarmRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatAlarmService {

    private final ChatAlarmRepository chatAlarmRepository;
    private final MemberRepository memberRepository;
    
    public void createChatAlarm(Long chatRoomNo, Long receiverNo, ChatMsg chatMsg) {
        Member receiver = memberRepository.findById(receiverNo)
            .orElseThrow(() -> new RuntimeException("❌ 수신자 멤버 없음"));

        ChatAlarm alarm = ChatAlarm.builder()
            .memberNo(receiver)
            .chatMsgNo(chatMsg)
            .readStatus("N")
            .build();

        chatAlarmRepository.save(alarm);
    }
    

}
