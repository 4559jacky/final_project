package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.entity.ChatMsg;

import jakarta.persistence.criteria.Join;

public class ChatMsgSpecification {
	
	public static Specification<ChatMsg> chatRoomNoEquals(Long chatRoomNo){
		return (root, query, criteriaBuilder) -> {
	        Join<Object, Object> formJoin = root.join("chatRoomNo"); // chatMsg.chatRoomNo
	        return criteriaBuilder.equal(formJoin.get("chatRoomNo"), chatRoomNo);
	    };
	}
}
