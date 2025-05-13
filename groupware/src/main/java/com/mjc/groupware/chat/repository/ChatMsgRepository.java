package com.mjc.groupware.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.chat.entity.ChatMsg;
import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.member.entity.Member;

public interface ChatMsgRepository  extends JpaRepository<ChatMsg, Long>{

	List<ChatMsg> findAll(Specification<ChatMsg> spec);
	
	// ChatMsgRepository.java
	int countByChatRoomNoAndSendDateAfterAndMemberNoNot(ChatRoom chatRoom, LocalDateTime sendDate, Member member);

	@Query("SELECT m FROM ChatMsg m LEFT JOIN FETCH m.attachNo WHERE m.chatRoomNo.chatRoomNo = :roomNo ORDER BY m.sendDate ASC")
	List<ChatMsg> findAllWithAttachByChatRoomNo(@Param("roomNo") Long roomNo);

}
