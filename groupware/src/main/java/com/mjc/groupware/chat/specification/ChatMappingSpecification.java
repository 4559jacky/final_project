package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.dto.ChatMappingDto;
import com.mjc.groupware.chat.entity.ChatMapping;

import jakarta.persistence.criteria.Predicate;

public class ChatMappingSpecification {
	// Define any custom query methods here if needed

	  public static Specification<ChatMapping> ChatRoomNoEqualsAndMemberNoEquals(ChatMappingDto dto) {
	        return (root, query, cb) -> {
	            Predicate chatRoomNo = cb.equal(root.get("chatRoomNo").get("chatRoomNo"), dto.getChat_room_no());
	            Predicate memberNo = cb.equal(root.get("memberNo").get("memberNo"), dto.getMember_no());
	            return cb.and(chatRoomNo, memberNo);
	        };
	    }
}
