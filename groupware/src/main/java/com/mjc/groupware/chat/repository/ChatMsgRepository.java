package com.mjc.groupware.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.member.entity.Member;

public interface ChatMsgRepository  extends JpaRepository<ChatMsg, Long>{

	List<ChatMsg> findAll(Specification<ChatMsg> spec);
	
	// ChatMsgRepository.java
	int countByChatRoomNoAndSendDateAfterAndMemberNoNot(ChatRoom chatRoom, LocalDateTime sendDate, Member member);


}
