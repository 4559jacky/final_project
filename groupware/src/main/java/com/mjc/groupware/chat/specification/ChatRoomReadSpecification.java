package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.dto.ChatRoomReadDto;
import com.mjc.groupware.chat.entity.ChatRoomRead;

import jakarta.persistence.criteria.Predicate;

public class ChatRoomReadSpecification {

	 public static Specification<ChatRoomRead> ChatRoomNoEqualsAndMemberNoEquals(ChatRoomReadDto dto) {
	        return (root, query, cb) -> {
	            Predicate chatRoomNo = cb.equal(root.get("chatRoomNo").get("chatRoomNo"), dto.getChat_room_no());
	            Predicate memberNo = cb.equal(root.get("memberNo").get("memberNo"), dto.getMember_no());
	            return cb.and(chatRoomNo, memberNo);
	        };
	    }
}
