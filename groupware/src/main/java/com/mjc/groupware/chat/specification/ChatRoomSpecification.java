package com.mjc.groupware.chat.specification;

import org.springframework.data.jpa.domain.Specification;

import com.mjc.groupware.chat.entity.ChatRoom;
import com.mjc.groupware.member.entity.Member;

public class ChatRoomSpecification {

	public static Specification<ChatRoom> fromMemberEquals(Member member){
		return (root, query , criteriaBuilder) -> 
		criteriaBuilder.equal(root.get("memberNo"),member);
	}
	
	public static Specification<ChatRoom> toMemberEquals(Member member){
		return (root, query , criteriaBuilder) -> 
		criteriaBuilder.in(root.get("mappings"));
	}
	
}
