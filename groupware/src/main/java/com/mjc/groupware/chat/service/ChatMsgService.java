package com.mjc.groupware.chat.service;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.repository.ChatMsgRepository;
import com.mjc.groupware.chat.specification.ChatMsgSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMsgService {

	private final ChatMsgRepository chatMsgRepository;
	
	// 채팅 메세지 조회 selectChatMsg
	public List<ChatMsg> selectChatMsgList(Long chatRoomNo) {
		
		Specification<ChatMsg> spec = (root, query, criteriaBuilder) -> null;
		spec = spec.and(ChatMsgSpecification.chatRoomNoEquals(chatRoomNo));
		
		List<ChatMsg> resultList = chatMsgRepository.findAll(spec);
		
		return resultList;
	}
	
	
}
