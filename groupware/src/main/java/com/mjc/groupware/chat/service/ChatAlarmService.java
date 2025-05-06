package com.mjc.groupware.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.chat.entity.ChatAlarm;
import com.mjc.groupware.chat.entity.ChatMapping;
import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.chat.repository.ChatRoomRepository;
import com.mjc.groupware.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatAlarmService {

	private ChatRoomRepository chatRoomRepository;
	
	/*
	 * @Transactional public void createAlarms(Long chatRoomNo, Long senderNo,
	 * ChatMsg chatMsg) { ChatRoom room = chatRoomRepository.findById(chatRoomNo)
	 * .orElseThrow(() -> new RuntimeException("채팅방 없음"));
	 * 
	 * for (ChatMapping mapping : room.getMappings()) { Member receiver =
	 * mapping.getMemberNo(); if (receiver == null) continue;
	 * 
	 * // 1. 자기 자신은 알림 제외 if (receiver.getMemberNo().equals(senderNo)) continue;
	 * 
	 * // 2. 현재 채팅방 들어와있는 사람도 제외 boolean isInRoom =
	 * webSocketSessionManager.isInRoom(receiver.getMemberNo(), chatRoomNo); if
	 * (isInRoom) continue;
	 * 
	 * ChatAlarm alarm = ChatAlarm.builder() .receiverNo(receiver)
	 * .chatMsgNo(chatMsg) .build();
	 * 
	 * chatAlarmRepository.save(alarm); } }
	 */

}
